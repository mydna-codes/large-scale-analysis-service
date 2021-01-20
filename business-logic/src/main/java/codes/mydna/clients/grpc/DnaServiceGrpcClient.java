package codes.mydna.clients.grpc;

import codes.mydna.analysis_result.lib.enums.Status;
import codes.mydna.auth.common.models.User;
import codes.mydna.clients.grpc.models.CheckedEntity;
import codes.mydna.sequence_bank.lib.Dna;
import codes.mydna.sequence_bank.lib.grpc.DnaServiceGrpc;
import codes.mydna.sequence_bank.lib.grpc.DnaServiceProto;
import codes.mydna.sequence_bank.lib.grpc.mappers.GrpcDnaMapper;
import codes.mydna.sequence_bank.lib.grpc.mappers.GrpcUserMapper;
import com.kumuluz.ee.grpc.client.GrpcChannelConfig;
import com.kumuluz.ee.grpc.client.GrpcChannels;
import com.kumuluz.ee.grpc.client.GrpcClient;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.net.ssl.SSLException;
import java.util.logging.Logger;

@ApplicationScoped
public class DnaServiceGrpcClient {

    private final static Logger LOG = Logger.getLogger(DnaServiceGrpcClient.class.getName());

    private DnaServiceGrpc.DnaServiceBlockingStub dnaServiceBlockingStub;

    @PostConstruct
    public void init() {
        try {
            GrpcChannels clientPool = GrpcChannels.getInstance();
            GrpcChannelConfig config = clientPool.getGrpcClientConfig("sequence-bank-grpc-client");
            GrpcClient client = new GrpcClient(config);

            dnaServiceBlockingStub = DnaServiceGrpc.newBlockingStub(client.getChannel()).withWaitForReady();

            LOG.info("Grpc client " + DnaServiceGrpcClient.class.getSimpleName() + " initialized.");

        } catch (SSLException e) {
            LOG.warning(e.getMessage());
        }
    }

    public CheckedEntity<Dna> getDna(String id, User user) {

        CheckedEntity<Dna> entity = new CheckedEntity<>();

        DnaServiceProto.DnaRequest request = DnaServiceProto.DnaRequest.newBuilder()
                .setServiceType(DnaServiceProto.DnaRequest.ServiceType.LARGE_SCALE)
                .setId(id)
                .setUser(GrpcUserMapper.toGrpcUser(user))
                .build();

        try {
            DnaServiceProto.DnaResponse response;
            response = dnaServiceBlockingStub.getDna(request);

            if (response.hasDna()) {
                Dna dna = GrpcDnaMapper.fromGrpcDna(response.getDna());
                entity.setEntity(dna);
                entity.setStatus(Status.OK);
            } else {
                entity.setStatus(Status.ENTITY_NOT_FOUND);
            }
            return entity;

        } catch (Exception e) {

            if (e.getMessage().equals(io.grpc.Status.NOT_FOUND.getCode().name())) {
                entity.setStatus(Status.ENTITY_NOT_FOUND);
            } else if (e.getMessage().equals(io.grpc.Status.PERMISSION_DENIED.getCode().name())) {
                entity.setStatus(Status.UNAUTHORIZED);
            } else {
                entity.setStatus(Status.INTERNAL_SERVER_ERROR);
            }
            return entity;
        }

    }

}

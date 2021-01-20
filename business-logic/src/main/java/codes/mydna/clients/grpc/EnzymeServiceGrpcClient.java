package codes.mydna.clients.grpc;

import codes.mydna.auth.common.models.User;
import codes.mydna.sequence_bank.lib.Enzyme;
import codes.mydna.sequence_bank.lib.grpc.EnzymeServiceGrpc;
import codes.mydna.sequence_bank.lib.grpc.EnzymeServiceProto;
import codes.mydna.sequence_bank.lib.grpc.mappers.GrpcEnzymeMapper;
import codes.mydna.sequence_bank.lib.grpc.mappers.GrpcUserMapper;
import com.kumuluz.ee.grpc.client.GrpcChannelConfig;
import com.kumuluz.ee.grpc.client.GrpcChannels;
import com.kumuluz.ee.grpc.client.GrpcClient;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.net.ssl.SSLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ApplicationScoped
public class EnzymeServiceGrpcClient {

    private final static Logger LOG = Logger.getLogger(EnzymeServiceGrpcClient.class.getName());

    private EnzymeServiceGrpc.EnzymeServiceBlockingStub enzymeServiceBlockingStub;

    @PostConstruct
    public void init(){
        try {
            GrpcChannels clientPool = GrpcChannels.getInstance();
            GrpcChannelConfig config = clientPool.getGrpcClientConfig("sequence-bank-grpc-client");
            GrpcClient client = new GrpcClient(config);

            enzymeServiceBlockingStub = EnzymeServiceGrpc.newBlockingStub(client.getChannel());

            LOG.info("Grpc client " + EnzymeServiceGrpcClient.class.getSimpleName() + " initialized.");

        } catch (SSLException e) {
            LOG.warning(e.getMessage());
        }
    }

    public List<Enzyme> getMultipleEnzymes(List<String> ids, User user){

        // If ids are not passed, don't call grpc and return empty list
        if(ids == null || ids.isEmpty()){
            return new ArrayList<>();
        }

        EnzymeServiceProto.MultipleEnzymesRequest request = EnzymeServiceProto.MultipleEnzymesRequest.newBuilder()
                .addAllId(ids)
                .setUser(GrpcUserMapper.toGrpcUser(user))
                .build();

        try {
            return enzymeServiceBlockingStub.getMultipleEnzymes(request)
                    .getEnzymeList()
                    .stream()
                    .map(GrpcEnzymeMapper::fromGrpcEnzyme)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        }catch (Exception e) {

            LOG.severe("Failed to connect to gRPC client: " + e.getMessage());
            return new ArrayList<>();
        }
    }

}

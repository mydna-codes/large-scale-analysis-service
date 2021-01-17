package codes.mydna.clients.grpc;

import codes.mydna.auth.common.models.User;
import codes.mydna.lib.Gene;
import codes.mydna.lib.grpc.GeneServiceGrpc;
import codes.mydna.lib.grpc.GeneServiceProto;
import codes.mydna.lib.grpc.mappers.GrpcGeneMapper;
import codes.mydna.lib.grpc.mappers.GrpcUserMapper;
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
public class GeneServiceGrpcClient {

    private final static Logger LOG = Logger.getLogger(GeneServiceGrpcClient.class.getName());

    private GeneServiceGrpc.GeneServiceBlockingStub geneServiceBlockingStub;

    @PostConstruct
    public void init(){
        try {
            GrpcChannels clientPool = GrpcChannels.getInstance();
            GrpcChannelConfig config = clientPool.getGrpcClientConfig("sequence-bank-grpc-client");
            GrpcClient client = new GrpcClient(config);

            geneServiceBlockingStub = GeneServiceGrpc.newBlockingStub(client.getChannel());

            LOG.info("Grpc client " + GeneServiceGrpcClient.class.getSimpleName() + " initialized.");

        } catch (SSLException e) {
            LOG.warning(e.getMessage());
        }
    }

    public List<Gene> getMultipleGenes(List<String> ids, User user){

        // If ids are not passed, don't call grpc and return empty list
        if(ids == null || ids.isEmpty()){
            return new ArrayList<>();
        }

        GeneServiceProto.MultipleGenesRequest request = GeneServiceProto.MultipleGenesRequest.newBuilder()
                .addAllId(ids)
                .setUser(GrpcUserMapper.toGrpcUser(user))
                .build();

        try {
            return geneServiceBlockingStub.getMultipleGenes(request)
                    .getGeneList()
                    .stream()
                    .map(GrpcGeneMapper::fromGrpcGene)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        }catch (Exception e) {

            LOG.severe("Failed to connect to gRPC client: " + e.getMessage());
            return new ArrayList<>();
        }


    }

}

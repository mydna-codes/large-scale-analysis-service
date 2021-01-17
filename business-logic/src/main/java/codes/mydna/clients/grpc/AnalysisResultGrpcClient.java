package codes.mydna.clients.grpc;

import codes.mydna.auth.common.models.User;
import codes.mydna.lib.AnalysisResult;
import codes.mydna.lib.enums.Status;
import codes.mydna.lib.grpc.AnalysisResultProto;
import codes.mydna.lib.grpc.AnalysisResultServiceGrpc;
import codes.mydna.lib.grpc.mappers.GrpcAnalysisResultMapper;
import codes.mydna.lib.grpc.mappers.GrpcUserMapper;
import com.kumuluz.ee.grpc.client.GrpcChannelConfig;
import com.kumuluz.ee.grpc.client.GrpcChannels;
import com.kumuluz.ee.grpc.client.GrpcClient;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.net.ssl.SSLException;
import java.util.logging.Logger;

@ApplicationScoped
public class AnalysisResultGrpcClient {

    private final static Logger LOG = Logger.getLogger(AnalysisResultGrpcClient.class.getName());

    private AnalysisResultServiceGrpc.AnalysisResultServiceBlockingStub stub;

    @PostConstruct
    public void init() {
        try {
            GrpcChannels clientPool = GrpcChannels.getInstance();
            GrpcChannelConfig config = clientPool.getGrpcClientConfig("analysis-result-grpc-client");
            GrpcClient client = new GrpcClient(config);

            stub = AnalysisResultServiceGrpc.newBlockingStub(client.getChannel()).withWaitForReady();

            LOG.info("Grpc client " + AnalysisResultGrpcClient.class.getSimpleName() + " initialized.");

        } catch (SSLException e) {
            LOG.warning(e.getMessage());
        }
    }

    public AnalysisResult insertAnalysisResult(AnalysisResult result, User user) {

        AnalysisResultProto.AnalysisResultInsertionRequest request;
        request = AnalysisResultProto.AnalysisResultInsertionRequest.newBuilder()
                .setServiceType(AnalysisResultProto.AnalysisResultInsertionRequest.ServiceType.LARGE_SCALE)
                .setUser(GrpcUserMapper.toGrpcUser(user))
                .setAnalysisResult(GrpcAnalysisResultMapper.toGrpcAnalysisResult(result))
                .build();

        try {
            AnalysisResultProto.AnalysisResultInsertionResponse response;
            response = stub.insertAnalysisResult(request);

            result.setId(response.getAnalysisResultId());
            result.setStatus(Status.OK);

        } catch (Exception e) {

            if (e.getMessage().equals(io.grpc.Status.NOT_FOUND.getCode().name())) {
                result.setStatus(Status.ENTITY_NOT_FOUND);
            } else if (e.getMessage().equals(io.grpc.Status.PERMISSION_DENIED.getCode().name())) {
                result.setStatus(Status.UNAUTHORIZED);
            } else {
                result.setStatus(Status.INTERNAL_SERVER_ERROR);
            }
        }

        return result;
    }

}

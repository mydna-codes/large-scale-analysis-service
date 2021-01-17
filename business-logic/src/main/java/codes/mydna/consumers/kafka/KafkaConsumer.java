package codes.mydna.consumers.kafka;

import codes.mydna.lib.large_scale.LargeScaleAnalysisRequest;
import codes.mydna.services.AnalysisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kumuluz.ee.streaming.common.annotations.StreamListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.logging.Logger;

@ApplicationScoped
public class KafkaConsumer {

    private static final Logger LOG = Logger.getLogger(KafkaConsumer.class.getName());

    @Inject
    private AnalysisService analysisService;

    @StreamListener(topics = {"large_scale_analysis"})
    public void onMessage(ConsumerRecord<String, String> record) {

        LargeScaleAnalysisRequest request;
        try {
            ObjectMapper mapper = new ObjectMapper();
            request = mapper.readValue(record.value(), LargeScaleAnalysisRequest.class);
        } catch (JsonProcessingException e) {
            LOG.severe("Failed to deserialize AnalysisRequest object!");
            return;
        }

        analysisService.analyze(request.getAnalysisRequest(), request.getUser());
    }

}
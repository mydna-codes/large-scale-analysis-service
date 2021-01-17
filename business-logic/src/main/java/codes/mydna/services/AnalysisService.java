package codes.mydna.services;

import codes.mydna.auth.common.models.User;
import codes.mydna.lib.AnalysisRequest;

/**
 * @see codes.mydna.services.impl.AnalysisServiceImpl
 */
public interface AnalysisService {

    void analyze(AnalysisRequest request, User user);

}

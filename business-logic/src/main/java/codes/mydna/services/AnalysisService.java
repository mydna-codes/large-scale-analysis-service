package codes.mydna.services;

import codes.mydna.analysis_result.lib.AnalysisRequest;
import codes.mydna.auth.common.models.User;

/**
 * @see codes.mydna.services.impl.AnalysisServiceImpl
 */
public interface AnalysisService {

    void analyze(AnalysisRequest request, User user);

}

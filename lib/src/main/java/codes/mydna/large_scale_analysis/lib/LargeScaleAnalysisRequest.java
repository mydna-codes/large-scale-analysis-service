package codes.mydna.large_scale_analysis.lib;

import codes.mydna.auth.common.models.User;
import codes.mydna.analysis_result.lib.AnalysisRequest;

public class LargeScaleAnalysisRequest {

    private User user;
    private AnalysisRequest analysisRequest;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AnalysisRequest getAnalysisRequest() {
        return analysisRequest;
    }

    public void setAnalysisRequest(AnalysisRequest analysisRequest) {
        this.analysisRequest = analysisRequest;
    }
}

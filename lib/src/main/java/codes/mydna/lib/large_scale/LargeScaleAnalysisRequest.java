package codes.mydna.lib.large_scale;

import codes.mydna.auth.common.models.User;
import codes.mydna.lib.AnalysisRequest;

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

package backend;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;

public class TwoStepOAuth extends DefaultApi10a {
    @Override
    public String getAccessTokenEndPoint() {
        return null;
    }

    @Override
    public String getAuthorizationUrl(Token arg0) {
        return null;
    }

    @Override
    public String getRequestTokenEndPoint() {
        return null;
    }
}
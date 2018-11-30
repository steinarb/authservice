package no.priv.bang.authservice.mocks;

import javax.servlet.http.HttpServletResponse;

abstract public class HttpResponseForRecordingStatus implements HttpServletResponse {

    private int status;

    @Override
    public void setStatus(int sc) {
        status = sc;
    }

    @Override
    public int getStatus() {
        return status;
    }

}
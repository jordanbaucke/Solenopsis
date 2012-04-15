package com.redhat.solenopsis.ws.impl;

import com.redhat.solenopsis.ws.LoginSvc;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;

/**
 *
 * Base class for any SFDC web service that needs the session id in the header.
 *
 * @author sfloess
 *
 */
public abstract class AbstractSessionIdBasedSvc<P> extends AbstractSvc<P> {    
    /**
     * The service we use to login.
     */
    private final LoginSvc loginSvc;
    
    /**
     * Return the login service.
     */
    protected LoginSvc getLoginSvc() {
        return loginSvc;
    }
    
    @Override
    protected String getServiceUrl() {
        return getLoginSvc().getServerUrl();
    }

    /**
     * Set the session id.
     */
    protected void setSessionId(BindingProvider bindingProvider, String sessionId) {
        final SessionIdInjectHandler handler = new SessionIdInjectHandler(sessionId);
        final List<Handler> handlerChain = new ArrayList<Handler>();
        
        handlerChain.add(handler);
        
        bindingProvider.getBinding().setHandlerChain(handlerChain);
        
        if (getLogger().isLoggable(Level.INFO)) {
            getLogger().log(Level.INFO, "Seting session id to [{0}]", sessionId);
        }
    }
    
    protected AbstractSessionIdBasedSvc(final LoginSvc loginSvc) {
        this.loginSvc = loginSvc;
    }
    
    /**
     * @{@inheritDoc}
     */
    @Override
    public P getPort() throws Exception {
        final P retVal = super.getPort();
        
        setSessionId((BindingProvider) retVal, getLoginSvc().getSessionId());
        
        return retVal;
    }
    
    @Override
    public void login() throws Exception {
        getLoginSvc().login();
    }
    
    @Override
    public boolean isLoggedIn() {
        return getLoginSvc().isLoggedIn();
    }
}

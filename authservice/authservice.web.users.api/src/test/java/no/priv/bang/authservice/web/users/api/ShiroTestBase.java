/*
 * Copyright 2024 Steinar Bang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations
 * under the License.
 */
package no.priv.bang.authservice.web.users.api;

import static org.apache.shiro.web.util.WebUtils.SAVED_REQUEST_KEY;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.Ini;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.SimpleAccountRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.env.IniWebEnvironment;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.subject.WebSubject;
import org.apache.shiro.web.util.SavedRequest;

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;

public class ShiroTestBase {

    private static WebSecurityManager securitymanager;
    private static SimpleAccountRealm realm;

    public ShiroTestBase() {
        super();
    }

    protected void loginUser(String username, String password) {
        HttpSession session = mock(HttpSession.class);
        MockHttpServletRequest dummyrequest = new MockHttpServletRequest();
        dummyrequest.setSession(session);
        MockHttpServletResponse dummyresponse = new MockHttpServletResponse();
        loginUser(dummyrequest, dummyresponse, username, password);
    }

    protected void loginUser(HttpServletRequest request, HttpServletResponse response, String username, String password) {
        WebSubject subject = createSubjectAndBindItToThread(request, response);
        UsernamePasswordToken token = new UsernamePasswordToken(username, password.toCharArray(), true);
        subject.login(token);
    }

    protected void removeWebSubjectFromThread() {
        ThreadContext.remove(ThreadContext.SUBJECT_KEY);
    }

    protected WebSubject createSubjectWithNullPrincipalAndBindItToThread() {
        WebSubject subject = mock(WebSubject.class);
        ThreadContext.bind(subject);
        return subject;
    }

    protected WebSubject createSubjectAndBindItToThread() {
        return createSubjectAndBindItToThread(getSecurityManager());
    }

    protected WebSubject createSubjectAndBindItToThread(WebSecurityManager webSecurityManager) {
        HttpSession session = mock(HttpSession.class);
        MockHttpServletRequest dummyrequest = new MockHttpServletRequest();
        dummyrequest.setSession(session);
        MockHttpServletResponse dummyresponse = new MockHttpServletResponse();
        return createSubjectAndBindItToThread(webSecurityManager, dummyrequest, dummyresponse);
    }

    protected WebSubject createSubjectAndBindItToThread(HttpServletRequest request, HttpServletResponse response) {
        return createSubjectAndBindItToThread(getSecurityManager(), request, response);
    }

    protected WebSubject createSubjectAndBindItToThread(WebSecurityManager webSecurityManager, HttpServletRequest request, HttpServletResponse response) {
        Session session = mock(Session.class);
        var savedRequest = new SavedRequest(request);
        when(session.getAttribute(SAVED_REQUEST_KEY)).thenReturn(savedRequest);
        WebSubject subject = (WebSubject) new WebSubject.Builder(webSecurityManager, request, response).session(session).buildSubject();
        ThreadContext.bind(subject);
        return subject;
    }

    public static WebSecurityManager getSecurityManager() {
        if (securitymanager == null) {
            IniWebEnvironment environment = new IniWebEnvironment();
            environment.setIni(Ini.fromResourcePath("classpath:test.shiro.ini"));
            environment.init();
            securitymanager = environment.getWebSecurityManager();
            realm = findRealmFromSecurityManager(securitymanager);
        }

        return securitymanager;
    }

    private static SimpleAccountRealm findRealmFromSecurityManager(WebSecurityManager securitymanager) {
        RealmSecurityManager realmSecurityManager = (RealmSecurityManager) securitymanager;
        Collection<Realm> realms = realmSecurityManager.getRealms();
        return (SimpleAccountRealm) realms.iterator().next();
    }

    public static SimpleAccount getShiroAccountFromRealm(String username) {
        if (realm == null) {
            getSecurityManager();
        }

        return findUserFromRealm(realm, username);
    }

    private static SimpleAccount findUserFromRealm(SimpleAccountRealm realm, String username) {
        try {
            Method getUserMethod = SimpleAccountRealm.class.getDeclaredMethod("getUser", String.class);
            getUserMethod.setAccessible(true);
            return (SimpleAccount) getUserMethod.invoke(realm, username);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

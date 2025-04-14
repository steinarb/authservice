/*
 * Copyright 2025 Steinar Bang
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
package no.priv.bang.authservice.web.security.cipherkey;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.apache.karaf.config.core.ConfigRepository;
import org.apache.shiro.crypto.cipher.AesCipherService;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import no.priv.bang.authservice.definitions.CipherKeyService;

/***
 * The rememberme functionality of apache shiro requires a cipher key, that:
 * <ol>
 * <li>Should be identical across all invocations and all applications using the same {@link SessionDAO}</li>
 * <li>Should not be configured in a visible place (because then remembered sessions can be hijacked)</li>
 * </ol>
 *
 * The default implementation of apache shiro creates a new cipherkey on component restarts which avoids
 * risk of hijacking, but also stops rememberme functionality from working properly, especially with
 * multiple applications sharing the same {@link SessionDAO}.
 *
 * This SCR component tries to solve the problem by
 * <ol>
 * <li>Creating a cipherkey on first component startup and store the key in apache config</li>
 * <li>Provide the OSGi service {@link CipherKeyService} which has a method returning the key</li>
 * </ol>
 *
 * The {@link CipherKeyService#getCipherKey()} method is used to set the cipher key of the {@link RememberMeManager}
 */
@Component( immediate=true )
public class ChipherKeyServiceProvider implements CipherKeyService {

    static final String CIPHERKEY_CONFIG_KEY = "cipherkey";

    private byte[] savedCipherKey;

    @Reference
    public ConfigRepository configRepository;

    @Activate
    public void activate(Map<String, Object> config) throws IOException {
        getCipherKeyFromConfigAndCreateNewCipherKeyIfMissing(config);
    }

    @Override
    public byte[] getCipherKey() {
        return savedCipherKey;
    }

    private void getCipherKeyFromConfigAndCreateNewCipherKeyIfMissing(Map<String, Object> config) throws IOException {
        savedCipherKey = Optional.ofNullable((String)config.get(CIPHERKEY_CONFIG_KEY)).map(c -> Base64.getDecoder().decode(c)).orElse(null);
        if (savedCipherKey == null) {
            var aesCipherService = new AesCipherService();
            savedCipherKey = aesCipherService.generateNewKey().getEncoded();
            saveCipherKeyInKarafConfig(savedCipherKey);
        }
    }

    private void saveCipherKeyInKarafConfig(byte[] cipherKey) throws IOException {
        var pid = getClass().getCanonicalName();
        var encodedCipherKey = Base64.getEncoder().encodeToString(cipherKey);
        configRepository.update(pid, Collections.singletonMap(CIPHERKEY_CONFIG_KEY, encodedCipherKey));
    }

}

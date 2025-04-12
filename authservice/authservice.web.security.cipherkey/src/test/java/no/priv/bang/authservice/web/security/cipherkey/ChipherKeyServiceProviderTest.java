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


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Base64;
import java.util.Collections;
import java.util.Map;

import org.apache.karaf.config.core.ConfigRepository;
import org.apache.shiro.crypto.cipher.AesCipherService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class ChipherKeyServiceProviderTest {

    @SuppressWarnings("unchecked")
    @Test
    void testGetCipherKeyWhenExistingKeyNotFound() throws Exception {
        var configRepository = mock(ConfigRepository.class);
        var component = new ChipherKeyServiceProvider();
        component.configRepository = configRepository;
        component.activate(Collections.emptyMap());
        var cipherkey = component.getCipherKey();
        assertThat(cipherkey).isNotEmpty();

        // Verify that freshly created key has been saved as a base64 encoded string value
        ArgumentCaptor<Map<String, Object>> argumentCaptor = ArgumentCaptor.forClass(Map.class);
        verify(configRepository).update(anyString(), argumentCaptor.capture());
        var savedCipherKey = Base64.getDecoder().decode((String)argumentCaptor.getValue().get(ChipherKeyServiceProvider.CIPHERKEY_CONFIG_KEY));
        assertThat(savedCipherKey).isEqualTo(cipherkey);
    }

    @Test
    void testGetCipherKeyWhenExistingKeyIsFound() throws Exception {
        var aesCipherService = new AesCipherService();
        var savedCipherKey = aesCipherService.generateNewKey().getEncoded();
        Map<String, Object> config = Collections.singletonMap("cipherkey", Base64.getEncoder().encodeToString(savedCipherKey));
        var configRepository = mock(ConfigRepository.class);
        var component = new ChipherKeyServiceProvider();
        component.configRepository = configRepository;
        component.activate(config);
        var cipherkey = component.getCipherKey();
        assertThat(cipherkey).isEqualTo(savedCipherKey);

        // Verify no save of key
        verifyNoInteractions(configRepository);
    }

}

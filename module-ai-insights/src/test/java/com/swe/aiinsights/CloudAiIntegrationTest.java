/*
 * -----------------------------------------------------------------------------
 *  File: AiClientServiceTest.java
 *  Owner: Nandhana Sunil
 *  Roll Number : 112201001
 *  Module : com.swe.aiinsights
 * -----------------------------------------------------------------------------
 */

package com.swe.aiinsights;


package com.swe.aiinsights;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swe.cloud.datastructures.Entity;
import com.swe.cloud.datastructures.TimeRange;
import com.swe.cloud.functionlibrary.CloudFunctionLibrary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GeminiKeyManagerTest {

    private CloudFunctionLibrary mockCloud;
    private GeminiKeyManager keyManager;

    @BeforeEach
    void setUp() {
        mockCloud = Mockito.mock(CloudFunctionLibrary.class);
        keyManager = new GeminiKeyManager(mockCloud); // Modify constructor to accept CloudFunctionLibrary for testing
    }

    @Test
    void testGetKeyList() {
        List<String> mockKeys = List.of("key1", "key2", "key3");
        CompletableFuture<Object> mockResponse = CompletableFuture.completedFuture(mockKeys);
        when(mockCloud.cloudGet(any(Entity.class))).thenReturn(mockResponse);

        List<String> keys = keyManager.getKeyList();
        assertEquals(mockKeys, keys);
    }

}

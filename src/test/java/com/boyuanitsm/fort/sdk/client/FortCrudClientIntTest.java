package com.boyuanitsm.fort.sdk.client;

import com.boyuanitsm.fort.sdk.FortSdkTest;
import com.boyuanitsm.fort.sdk.domain.SecurityUser;
import com.boyuanitsm.fort.sdk.exception.FortCrudException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;

/**
 * Test class for the FortCrudClient.
 *
 * @see FortCrudClient
 * @author hookszhang on 6/20/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = FortSdkTest.class)
@WebAppConfiguration
@IntegrationTest
public class FortCrudClientIntTest {

    @Autowired
    private FortCrudClient fortCrudClient;

    @Before
    public void setup() {
    }

    @Test
    public void testSignUp() throws IOException, FortCrudException {
        SecurityUser user = new SecurityUser();
        user.setLogin("A1111111");
        user.setPasswordHash("B1111111");
        fortCrudClient.signUp(user);
    }

}

//package com.zerobase.wishmarket.domain.auth;
//
//import com.zerobase.wishmarket.domain.user.controller.AuthController;
//import com.zerobase.wishmarket.domain.user.service.OAuthService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.boot.test.mock.mockito.MockBeans;
//import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
//import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(AuthController.class)
//@MockBeans({
//        @MockBean(JpaMetamodelMappingContext.class),
//        @MockBean(OAuthService.class)
//})
//public class OAuthLoginTest {
//
//    @Autowired
//    private MockMvc mvc;
//
//    private SecurityMockMvcRequestPostProcessors securityMockMvcRequestPostProcessors;
//
//    @Test
//    void googleLoginTest() throws Exception {
//
//        mvc.perform(post("/social-sign-in").with(oauth2Login()))
//                .andExpect(status().isOk());
//
//    }
//}

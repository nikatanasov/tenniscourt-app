package app.web;

import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.service.UserService;
import app.web.dto.EditProfileRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static app.TestBuilder.aRandomUser;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerApiTest {
    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAuthenticatedRequestToUserEditProfilePage_shouldReturnUserEditProfilePage() throws Exception {
        when(userService.getById(any())).thenReturn(aRandomUser());
        MockHttpServletRequestBuilder request = get("/users/profile/edit")
                .with(user(new AuthenticationMetadata(UUID.randomUUID(), "Vik123", "123123", UserRole.USER, true)));
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("edit-profile"))
                .andExpect(model().attributeExists("editProfileRequest"));

        verify(userService, times(1)).getById(any());
    }

    @Test
    void putAuthorizedRequestToEditUserProfile_happyPath() throws Exception {
        when(userService.getById(any())).thenReturn(aRandomUser());
        MockHttpServletRequestBuilder request = put("/users/profile/edit")
                .formField("firstName", "Viktor")
                .formField("lastName", "Alexandrov")
                .formField("username", "Vik123")
                .formField("email", "viktor@abv.bg")
                .formField("profilePicture", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSbGI7GnAUQN_SIjaxw1tKs5g8MwxL-JKbM3A&s")
                .formField("password", "123123")
                .with(user(new AuthenticationMetadata(UUID.randomUUID(), "Vik123", "123123", UserRole.USER, true)))
                .with(csrf());
        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        verify(userService, times(1)).getById(any());
        verify(userService, times(1)).editProfile(any(User.class), any(EditProfileRequest.class));
    }
}

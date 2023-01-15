package com.tracku.chris.tracku.Interfaces.User;
import com.tracku.chris.tracku.Utils.CustomRequests.Users.*;
import com.tracku.chris.tracku.Utils.CustomResponses.*;

public interface IUserService {
    RegistrationResponse registerUser(RegisterRequest request);
    AuthResponse signInUser(AuthRequest request);
    UpdateNameResponse updateUsername(UpdateNameRequest request);
    UpdatePasswordResponse updatePassword(UpdatePasswordRequest request);
    UserInfoResponse getUserInfo();
    DeleteUserResponse deleteUser(DeleteUserRequest request);
}

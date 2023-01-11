package com.tracku.chris.tracku.Interfaces.User;
import com.tracku.chris.tracku.Utils.CustomRequests.Users.AuthRequest;
import com.tracku.chris.tracku.Utils.CustomRequests.Users.RegisterRequest;
import com.tracku.chris.tracku.Utils.CustomResponses.AuthResponse;
import com.tracku.chris.tracku.Utils.CustomResponses.RegistrationResponse;

public interface IUserService {
    RegistrationResponse registerUser(RegisterRequest request);
    AuthResponse signInUser(AuthRequest request);

}

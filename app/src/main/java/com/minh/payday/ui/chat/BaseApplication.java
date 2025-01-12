package com.minh.payday.ui.chat;

import android.app.Application;

import com.sendbird.android.exception.SendbirdException;
import com.sendbird.android.handler.InitResultHandler;
import com.sendbird.uikit.SendbirdUIKit;
import com.sendbird.uikit.adapter.SendbirdUIKitAdapter;
import com.sendbird.uikit.interfaces.UserInfo;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        SendbirdUIKit.init(new SendbirdUIKitAdapter() {
            @Override
            public String getAppId() {
                return "27BCECDF-1B6B-4ADF-BA94-6F82F95447E1"; // Specify your Sendbird application ID.
            }

            @Override
            public String getAccessToken() {
                return "bb7a3085d87171f70c9b388f22c2bd2cbd53bf7c"; // Specify your user's access token.
            }

            @Override
            public UserInfo getUserInfo() {
                return new UserInfo() {
                    @Override
                    public String getUserId() {
                        return "user1";
                        // Use the ID of a user you've created on the dashboard.
                    }

                    @Override
                    public String getNickname() {
                        return "user1"; // Specify your user nickname. Optional.
                    }

                    @Override
                    public String getProfileUrl() {
                        return ""; // Specify your profile URL. Optional.
                    }
                };
            }

            @Override
            public InitResultHandler getInitResultHandler() {
                return new InitResultHandler() {
                    @Override
                    public void onMigrationStarted() {
                        // DB migration has started.
                    }

                    @Override
                    public void onInitFailed(SendbirdException e) {
                        // If DB migration fails, this method is called.
                        e.printStackTrace();
                    }

                    @Override
                    public void onInitSucceed() {
                        // If DB migration is successful, this method is called and you can proceed to the next step.
                        // You can update UI or notify the user that the initialization was successful here.
                    }
                };
            }
        }, this);
    }
}

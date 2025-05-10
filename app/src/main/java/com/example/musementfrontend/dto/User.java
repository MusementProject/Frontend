package com.example.musementfrontend.dto;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Parcelable {
    private Long id;
    private String googleId;
    private String username;
    private String nickname;
    private String email;
    private String profilePicture;
    private String bio;
    private String telegram;
    private String accessToken;

    protected User(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        googleId = in.readString();
        username = in.readString();
        nickname = in.readString();
        email = in.readString();
        profilePicture = in.readString();
        bio = in.readString();
        telegram = in.readString();
        accessToken = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(googleId);
        dest.writeString(username);
        dest.writeString(nickname);
        dest.writeString(email);
        dest.writeString(profilePicture);
        dest.writeString(bio);
        dest.writeString(telegram);
        dest.writeString(accessToken);
    }
}

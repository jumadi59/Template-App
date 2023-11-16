package com.app.core.network.response;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Created by JJ date on 10/10/2023.
 * Bengkulu, Indonesia.
 * Copyright (c) Company. All rights reserved.
 **/
public class GeneralResponse implements Parcelable {

    @SerializedName(value = "timestamp")
    private String timestamp;
    @SerializedName(value = "message")
    private String message;
    @SerializedName(value = "status")
    private int status;
    @SerializedName(value = "error")
    private String  error;
    private String icon;

    public GeneralResponse() {
    }

    public GeneralResponse(String timestamp, String message, int status, String error, String icon) {
        this.timestamp = timestamp;
        this.message = message;
        this.status = status;
        this.error = error;
        this.icon = icon;
    }

    public GeneralResponse(String message, int status, String error) {
        this.message = message;
        this.status = status;
        this.error = error;
    }

    public GeneralResponse(String message, int status) {
        this.message = message;
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    protected GeneralResponse(Parcel in) {
        timestamp = in.readString();
        message = in.readString();
        status = in.readInt();
        error = in.readString();
        icon = in.readString();
    }

    public static final Creator<GeneralResponse> CREATOR = new Creator<GeneralResponse>() {
        @Override
        public GeneralResponse createFromParcel(Parcel in) {
            return new GeneralResponse(in);
        }

        @Override
        public GeneralResponse[] newArray(int size) {
            return new GeneralResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(timestamp);
        dest.writeString(message);
        dest.writeInt(status);
        dest.writeString(error);
        dest.writeString(icon);
    }

    @Override
    public String toString() {
        return "GeneralResponse{" +
                "timestamp='" + timestamp + '\'' +
                ", message='" + message + '\'' +
                ", status=" + status +
                ", error='" + error + '\'' +
                ", icon='" + icon + '\'' +
                '}';
    }
}

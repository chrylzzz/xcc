package com.haiyisoft.entry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChannelEvent {
    public ChannelEvent() {
    }

    private String nodeUuid = "";
    private String uuid = "";
    private String peerUuid = "";
    private String direction = "";
    private String state = "";
    private String cidName = "";
    private String cidNumber = "";
    private String destNumber = "";
    private int createEpoch = 1;
    private int ringEpoch = 1;
    private int answerEpoch = 1;
    private int hangupEpoch = 1;
    private String peer[] = new String[]{};
    private Map<String, ArrayList> params = new HashMap<>();
    private String billsec = "";
    private String duration = "";
    private Boolean video = false;
    private String cause = "";
    private Boolean answered = false;
    private String ctrlUuid = "";

    public String getNodeUuid() {
        return nodeUuid;
    }

    public void setNodeUuid(String nodeUuid) {
        this.nodeUuid = nodeUuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPeerUuid() {
        return peerUuid;
    }

    public void setPeerUuid(String peerUuid) {
        this.peerUuid = peerUuid;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCidName() {
        return cidName;
    }

    public void setCidName(String cidName) {
        this.cidName = cidName;
    }

    public String getCidNumber() {
        return cidNumber;
    }

    public void setCidNumber(String cidNumber) {
        this.cidNumber = cidNumber;
    }

    public String getDestNumber() {
        return destNumber;
    }

    public void setDestNumber(String destNumber) {
        this.destNumber = destNumber;
    }

    public int getCreateEpoch() {
        return createEpoch;
    }

    public void setCreateEpoch(int createEpoch) {
        this.createEpoch = createEpoch;
    }

    public int getRingEpoch() {
        return ringEpoch;
    }

    public void setRingEpoch(int ringEpoch) {
        this.ringEpoch = ringEpoch;
    }

    public int getAnswerEpoch() {
        return answerEpoch;
    }

    public void setAnswerEpoch(int answerEpoch) {
        this.answerEpoch = answerEpoch;
    }

    public int getHangupEpoch() {
        return hangupEpoch;
    }

    public void setHangupEpoch(int hangupEpoch) {
        this.hangupEpoch = hangupEpoch;
    }

    public String[] getPeer() {
        return peer;
    }

    public void setPeer(String[] peer) {
        this.peer = peer;
    }

    public Map<String, ArrayList> getParams() {
        return params;
    }

    public void setParams(Map<String, ArrayList> params) {
        this.params = params;
    }

    public String getBillsec() {
        return billsec;
    }

    public void setBillsec(String billsec) {
        this.billsec = billsec;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Boolean getVideo() {
        return video;
    }

    public void setVideo(Boolean video) {
        this.video = video;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public Boolean getAnswered() {
        return answered;
    }

    public void setAnswered(Boolean answered) {
        this.answered = answered;
    }

    public String getCtrlUuid() {
        return ctrlUuid;
    }

    public void setCtrlUuid(String ctrlUuid) {
        this.ctrlUuid = ctrlUuid;
    }
}



package com.example.demo.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class FixCase {
    @Id
    private long caseId;
    private String amtId;
    private String reasonName;
    private Date problemStartTime;
    private Date problemEndTime;
    private String serialNumber;
    private String bankName;
    private String link;

    public FixCase() {
    }

    public FixCase(long caseId, String amtId, String reasonName,
                   Date problemStartTime, Date problemEndTime,
                   String serialNumber, String bankName, String link) {
        this.caseId = caseId;
        this.amtId = amtId;
        this.reasonName = reasonName;
        this.problemStartTime = problemStartTime;
        this.problemEndTime = problemEndTime;
        this.serialNumber = serialNumber;
        this.bankName = bankName;
        this.link = link;
    }

    public long getCaseId() {
        return caseId;
    }

    public void setCaseId(long caseId) {
        this.caseId = caseId;
    }

    public String getAmtId() {
        return amtId;
    }

    public void setAmtId(String amtId) {
        this.amtId = amtId;
    }

    public String getReasonName() {
        return reasonName;
    }

    public void setReasonName(String reasonName) {
        this.reasonName = reasonName;
    }

    public Date getProblemStartTime() {
        return problemStartTime;
    }

    public void setProblemStartTime(Date problemStartTime) {
        this.problemStartTime = problemStartTime;
    }

    public Date getProblemEndTime() {
        return problemEndTime;
    }

    public void setProblemEndTime(Date problemEndTime) {
        this.problemEndTime = problemEndTime;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return "FixCase{" +
                "caseId=" + caseId +
                ", amtId=" + amtId +
                ", reasonName='" + reasonName + '\'' +
                ", problemStartTime=" + problemStartTime +
                ", problemEndTime=" + problemEndTime +
                ", serialNumber='" + serialNumber + '\'' +
                ", bankName='" + bankName + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}

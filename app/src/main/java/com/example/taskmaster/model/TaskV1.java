package com.example.taskmaster.model;

public class TaskV1 {
    public int jobid;
    public double budget;
    public String job_title;
    public String job_domain;
    public String requirements;
    public String created_at;
    public String due_date;
    public String due_time;
    public String status;

    public TaskV1(){};
    @Override
    public String toString() {
        return "TaskV1{" +
                "jobid=" + jobid +
                ", budget=" + budget +
                ", job_title='" + job_title + '\'' +
                ", job_domain='" + job_domain + '\'' +
                ", requirements='" + requirements + '\'' +
                ", created_at='" + created_at + '\'' +
                ", due_date='" + due_date + '\'' +
                ", due_time='" + due_time + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    public int getJobid() {
        return jobid;
    }

    public void setJobid(int jobid) {
        this.jobid = jobid;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public String getJob_title() {
        return job_title;
    }

    public void setJob_title(String job_title) {
        this.job_title = job_title;
    }

    public String getJob_domain() {
        return job_domain;
    }

    public void setJob_domain(String job_domain) {
        this.job_domain = job_domain;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getDue_date() {
        return due_date;
    }

    public void setDue_date(String due_date) {
        this.due_date = due_date;
    }

    public String getDue_time() {
        return due_time;
    }

    public void setDue_time(String due_time) {
        this.due_time = due_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public TaskV1(int jobid, double budget, String job_title, String job_domain, String requirements, String created_at, String due_date, String due_time, String status) {
        this.jobid = jobid;
        this.budget = budget;
        this.job_title = job_title;
        this.job_domain = job_domain;
        this.requirements = requirements;
        this.created_at = created_at;
        this.due_date = due_date;
        this.due_time = due_time;
        this.status = status;
    }
}

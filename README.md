# Digital Drop-Off for Sakai

**STATUS:** This project is currently compatible with out-of-the-box Sakai 11.1.

### Building

```
mvn clean install
```

### Installation
```
mvn sakai:deploy
```

#### Database Changes
Tables should be created automatically or can be created with

MySQL
```
create table if not exists DDO_SUBMISSION_T (SUBMISSIONID bigint(11) not null auto_increment primary key, DOCUMENTREF varchar(4000), SUBMISSIONDATE timestamp default current_timestamp, SUBMITTEDBY varchar(99) not null, STATUS varchar(15), ASSIGNMENTTITLE varchar(255), INSTRUCTORREQUIREMENTS text, COURSETITLE varchar(255), INSTRUCTOR varchar(255), DUEDATE datetime, PRIMARYLANGUAGEISENGLISH boolean, PRIMARYLANGUAGE varchar(255), FEEDBACKFOCUS text)

create table if not exists DDO_FEEDBACK_T (FEEDBACKID bigint(11) not null auto_increment primary key, SUBMISSIONID bigint(11) not null, REVIEWEDBY varchar(99) not null, REVIEWDATE timestamp default current_timestamp, COMMENTS text, REVIEWEDDOCUMENTREF varchar(255),  foreign key (SUBMISSIONID) references ddo_submission_t (SUBMISSIONID))
```

Oracle
```
create table DDO_SUBMISSION_T (SUBMISSIONID number(11,0) not null primary key, DOCUMENTREF varchar2(4000), SUBMISSIONDATE timestamp default current_timestamp, SUBMITTEDBY varchar2(99) not null, STATUS varchar2(15), ASSIGNMENTTITLE varchar2(255), INSTRUCTORREQUIREMENTS clob, COURSETITLE varchar2(255), INSTRUCTOR varchar2(255), DUEDATE date, PRIMARYLANGUAGEISENGLISH char, PRIMARYLANGUAGE varchar2(255), FEEDBACKFOCUS clob);

create sequence SUBMISSIONID_SEQ start with 1 increment by 1 nomaxvalue;

create table DDO_FEEDBACK_T (FEEDBACKID number(11,0) not null primary key, SUBMISSIONID number(11,0) not null, REVIEWEDBY varchar2(99) not null, REVIEWDATE timestamp default current_timestamp, COMMENTS clob, REVIEWEDDOCUMENTREF varchar2(255), foreign key (SUBMISSIONID) references ddo_submission_t (SUBMISSIONID));

create sequence FEEDBACKID_SEQ start with 1 increment by 1 nomaxvalue;
```

#### Sakai Properties
There are several additional sakai.properties used.
```
ddo.staff.email.address = example@some.edu
ddo.staff.email.display = Write Place Mailbox <example@some.edu>
ddo.notification.email.display = Sakai <no-reply@sakai.some.edu>
```

#### Realm Changes

A Sakai /ddo realm must be created with two roles (ddoadmin & ddostaff). The maintain role is ddoadmin.

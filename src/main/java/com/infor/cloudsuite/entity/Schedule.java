package com.infor.cloudsuite.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.infor.cloudsuite.service.StringDefs;

@Entity
public class Schedule extends DatedDataEntity{

	private Long id;
	private String entityId;
	private ScheduleType type;
	private ScheduleStatus status;
	private String extraScheduleCommand;
	private Date scheduledAt;
	private String targetObject;

	@Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = true)
	public Date getScheduledAt() {
		return scheduledAt;
	}

	public void setScheduledAt(Date when) {
		this.scheduledAt = when;
	}
    
    @GenericGenerator(name = "sequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                @Parameter(name = "sequence_name", value = "Schedule_SEQ"),
                @Parameter(name = "initial_value", value = StringDefs.SEQ_INITIAL_VALUE),
                @Parameter(name = "optimizer", value = StringDefs.SEQ_OPTIMIZER),
                @Parameter(name = "increment_size", value = StringDefs.SEQ_INCREMENT) })
        @Id
        @GeneratedValue(generator = "sequenceGenerator")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
	
	public void setEntityId(Long entityId) {
		this.entityId = entityId.toString();
	}

	@Enumerated(EnumType.STRING)
	public ScheduleType getType() {
		return type;
	}

	public void setType(ScheduleType type) {
		this.type = type;
	}

	@Enumerated(EnumType.STRING)
	public ScheduleStatus getStatus() {
		return status;
	}

	public void setStatus(ScheduleStatus status) {
		this.status = status;
	}

	public String getExtraScheduleCommand() {
		return extraScheduleCommand;
	}

	public void setExtraScheduleCommand(String extraScheduleCommand) {
		this.extraScheduleCommand = extraScheduleCommand;
	}

	@Override
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
	public Date getCreatedAt() {
		return super.createdAt;
	}
	
	@Override
    @Temporal(TemporalType.TIMESTAMP)
	public Date getUpdatedAt() {
		return super.updatedAt;
	}

    public String getTargetObject() {
        return targetObject;
    }

    public void setTargetObject(String targetObject) {
        this.targetObject = targetObject;
    }
}

package com.bobocode.bibernate.integration.entity;

import com.bobocode.bibernate.annotation.Column;
import com.bobocode.bibernate.annotation.Entity;
import com.bobocode.bibernate.annotation.Id;
import com.bobocode.bibernate.annotation.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table("date_times")
public class DateTestEntity {

    @Id
    private Long id;

    @Column("local_date")
    private LocalDate localDate;
    @Column("local_date_time")
    private LocalDateTime localDateTime;
    @Column("local_time")
    private LocalTime localTime;
    @Column("zoned_date_time")
    private ZonedDateTime zonedDateTime;

}

package com.bobocode.bibernate.integration.entity;

import com.bobocode.bibernate.annotation.Column;
import com.bobocode.bibernate.annotation.Entity;
import com.bobocode.bibernate.annotation.Id;
import com.bobocode.bibernate.annotation.Table;
import lombok.Data;
import lombok.experimental.Accessors;

@Table("persons")
@Data
@Entity
@Accessors(fluent = true)
public class Person {

    @Id
    @Column("id")
    private Long id;

    @Column("name")
    private String name;

    @Column("age")
    private Integer age;
}

# Bibernate

## Java ORM framework

---

## What is Bibernate?

Bibernate is a Java Object-Relational Mapping (ORM) framework which can help <b>work with
database in object-oriented manner</b>

---

## How to use

There are multiple steps you need to perform to correctly use Bibernate:

1. Create `persistence.yml` [resource file](#persistenceyml-file-example) with information about your database
2. Create [entities](#entity-declaration-example), that represent table from the database.
    * Mark them with the [`@Entity`](src/main/java/com/bobocode/bibernate/annotation/Entity.java) annotation.
    * Add field that represents primary key of relevant table and annotated with
      the [`@Id`](src/main/java/com/bobocode/bibernate/annotation/Id.java)
3. Create [`Session`](src/main/java/com/bobocode/bibernate/session/Session.java)
   object to interact with a persistence context. Look at
   the [example](#sessionsrcmainjavacombobocodebibernatesessionsessionjava-creation-example) below

#### `persistence.yml` file example:

```yaml
persistenceUnit:
  name: h2
  dataSource:
    jdbcUrl: jdbc:h2:mem:testdb
    user: sa
    password: password
  dialect: h2
```

#### Entity declaration example:

```java
import com.bobocode.bibernate.annotation.Column;
import com.bobocode.bibernate.annotation.Entity;
import com.bobocode.bibernate.annotation.Id;
import com.bobocode.bibernate.annotation.Table;
import lombok.Data;

@Data // not sure about this
@Entity
public class Product {

    @Id
    private Long id;

    private String name;

    private Double price;
}
```

#### [Session](src/main/java/com/bobocode/bibernate/session/Session.java) creation example:

```java
import com.bobocode.bibernate.Persistence;
import com.bobocode.bibernate.session.Session;
import com.bobocode.bibernate.session.SessionFactory;

import java.util.Optional;

public class SessionCreationExample {

    public static void main(String[] args) {
        SessionFactory sessionFactory = Persistence.createSessionFactory("default");

        try (Session session = sessionFactory.createSession()) {
            // use session here
        }
    }
}
```

## Dive into Bibernate

---
Bibernate is inspired by the project called [Hibernate ORM](https://hibernate.org/) -
object/relational mapping (ORM) framework. Although it does not implement the Java
Persistence API (JPA) specification, its API is based on object names of Hibernate.

### Major classes and interfaces structure

---
![](Major%20Classes%20Structure.png)

[`Persistence`](src/main/java/com/bobocode/bibernate/Persistence.java) class contains a static
method to obtain [`SessionFactory`](src/main/java/com/bobocode/bibernate/session/SessionFactory.java) <br>
[`SessionFactory`](src/main/java/com/bobocode/bibernate/session/SessionFactory.java) is a
factory for [`Session`](src/main/java/com/bobocode/bibernate/session/Session.java).
It can create and manage multiple Session instances <br>
[`Session`](src/main/java/com/bobocode/bibernate/session/Session.java)
manages the persistence operations on [`Entities`](src/main/java/com/bobocode/bibernate/annotation/Entity.java) <br>
[`Entity`](src/main/java/com/bobocode/bibernate/annotation/Entity.java) is a persistent object.
It corresponds to records inside a database table <br>
[`Transaction`](src/main/java/com/bobocode/bibernate/transaction/Transaction.java) maintains
operations for each Session. It can rather perform commit or rollback operations

### What is a persistence unit

---
[What is persistence unit, what does it stand for.
How to describe persistence units with .yml file]
A persistence unit defines a set of all `Entity` classes that are managed
by `Session` instances in an application.
This set of entity classes represents the data contained within a single data
store.

Persistence units are defined by the `persistence.yml` configuration file. Here is an example `persistence.yml` file:

```yaml
persistenceUnit:
  name: mysql
  dataSource:
    jdbcUrl: jdbc:mysql://127.0.0.1:3306/test_database
    user: root
    password: root
  dialect: h2
```

### What is an entity

---
[

* ~~What is entity, how to define it~~.
* ~~Necessary annotations (@Entity, @Id, @Table)~~
* & methods (constructor, maybe getters, setters).
* relations mapping ??
* ~~Entity states (transient, persisted, detached, removed)~~
* how to get entity in specific state ??
* ]

Entity is a class that represents table in the database. Each entity instance corresponds to a row in that table.
Entity fields or properties represent columns of the related table.
Class is defined as entity if it's annotated with `@Entity` and has field annotated with `@Id`, which represents primary
key.

You can use `@Table` annotation to specify table name. This annotation is optional. The lowercase class name is used
as table name if annotation is omitted.

```java
import com.bobocode.bibernate.annotation.Entity;
import com.bobocode.bibernate.annotation.Id;
import com.bobocode.bibernate.annotation.Table;

@Entity
@Table(value = "products")
public class Product {
    @Id
    private Long id;
}
```

You can use `@Column` annotation to specify column name. This annotation is optional. The lowercase field name is used
as column name if annotation is omitted.

```java
import com.bobocode.bibernate.annotation.Column;
import com.bobocode.bibernate.annotation.Entity;
import com.bobocode.bibernate.annotation.Id;
import com.bobocode.bibernate.annotation.Table;

@Entity
@Table(value = "products")
public class Product() {
    @Id
    private Long id;

    @Column(value = "name")
    private String productName;
}
```

Every entity naturally has a lifecycle within the framework – it's either in a transient, persistent (or managed),
detached or deleted state.

* **Transient entity** has neither any representation in the datastore nor in the current `Session`.
  A transient entity is simply a POJO without any identifier.
* **Persistent entity** exist in the database, and persistent context tracks all the changes done
  on the persistent entity by the client code. A persistent entity is mapped to a specific database row,
  identified by the ID field. `Session` is responsible for tracking all changes done to a managed entity
  and propagating these changes to database.
* **Detached entity** has a representation in the database, but it is not managed by the `Session`.
  Any changes to a detached entity will not be reflected in the database, and vice-versa.
* Removed entity is an object that was being persistent entity and now this has been passed to the session’s `remove()`
  method.

### What is [Session](src/main/java/com/bobocode/bibernate/session/Session.java)

[Session methods (maybe use some table: method name - description)]



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
2. Create [entities](#entity-declaration-example), that represent table from the database. Mark them with
   the [`@Entity`](src/main/java/com/bobocode/bibernate/annotation/Entity.java) annotation
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

@Table("products")
@Data
@Entity
public class Product {

    @Id
    @Column("id")
    private Long id;

    @Column("name")
    private String name;

    @Column("price")
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

#### Object Structure
[Object structure: Persistence -> SessionFactory -> Session, Entity, Dialect. 
Create a schema for example]

#### What is persistence unit
[What is persistence unit, what does it stand for. 
How to describe persistence units with .yml file]

#### What is entity
[What is entity, how to define it. Necessary annotations (@Entity, @Id, @Table) & 
methods (constructor, maybe getters, setters). 
Entity states (transient, persisted, detached, removed)]

#### What is [Session](src/main/java/com/bobocode/bibernate/session/Session.java)
[Session methods (maybe use some table: method name - description)]



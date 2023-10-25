[toc]

# Sky vs Reggie take out

I learned a lot from Reggie these months but get a little tired of it, then I see this upgraded projects on 2023.10.
25.  My current philosophy is to traverse enough projects to better understand the spring projects tech, so suspend deeppen into Reggie and start to learn this.

It cost too much time to review an old project, so I decide to make notes simultaneously when I watch the videos from [bilibili](https://www.bilibili.com/video/BV1TP411v7v6?p=5&vd_source=2cdc3de199e29dc4f7a75c883bb0d11d).

The key upgrade is as follows:

1. Separated front and backend projects. Reggie use springboot to take over front resources, but better option is user nginx to take over front packages(not source code).
2. Use spring task to calculate the data metric.

<img src="./project_basic.assets/image-20231025163141869.png" alt="image-20231025163141869" style="zoom: 33%;" />

# Front 

Nginx use localhost:80 as main page (default port is 80), copy front resources into html folder.

# Backend structure

- Sky-take-out: parent to control dependency version and children modules.
- Sky-common: store public class, eg tools, common variables, exceptions.
- Sky-pojo: store entity, vo, dto.
- Sky-server: store configurations, controllers, services, mappers.

## 1. sky-common contains what?

<img src="./project_basic.assets/image-20231025191641772.png" alt="image-20231025191641772" style="zoom: 50%;" />

Common module is dependent by other modules as a while.

- context stores something in ThreadLocal like userId
- exception stores self defined exceptions
- json stores some json converters or object mappings
- properties makes spring configuration into property class
- result is a return data structure
- utils stores some user defined utils like SMSUtils

## 2. Sky-pojo contains what?

![image-20231025192139835](./project_basic.assets/image-20231025192139835.png)

POJO: plain old java object, entity, dto and vo are all POJOs.

- Entity corresponds directly to tables in MySql or PostgreSql database.
- DTO: data transfer object is the data structure returned to the front, usually contains serveral entities or part of entities.
- VO: view object is very much like vo but I cannot tell the difference yet.

## 3. Sky-server contains what?

<img src="./project_basic.assets/image-20231025192552721.png" alt="image-20231025192552721" style="zoom: 50%;" />

Sky-server is much like reggie project, the booting class is stored in this module.

- config stores all configs, eg MVC, MyBatis, ObjectMapping, Swagger, etc.
- controller can be separated into admin and user
- interceptor stores permission control inteceptor

## Comparision and conclusions about 3 modules

Server module stores all info about business, easy to find all the APIs here.

Pojo treats all the data structure, it does not contains any functions but only fields.

Common module is more like a tool box and non-relative to business logic, it is designed not to be observed but works at backend silently.

# VCS by Git

VCS: version control system.

There are 2 ways to use git as version control tool:

1. Initiate a remote repo on GitHub and git clone to local.
2. Initate a git project in IDEA, new a remote repo on GitHub, then tie them on IDEA.

The steps about method 2 is as follows:
Day2 Employee & Category

[toc]

# Standard process

Let's take Employee as an example, it is a single-table CRUD case.

1. Requirements analyze. Product manager draw the product prototype html pages to show the functions and interactions.
2. Design. Experienced engineers design the api and table.
3. Devlopment. 
4. Test.
5. Perfect the scripts.

# Before devlopment

## prototype

<img src="./day2-employee-category.assets/image-20231101223215256.png" alt="image-20231101223215256" style="zoom:33%;" />

This is a prototype designed by PM. Be careful we have some data limitations like user account should be unique, phone numbers must be 11. Some is implemented on the front side (a phone number is valid or not), some on the backend side (accout is normal or frozen), some on the database side (unique key).

## api design

![image-20231101223922112](./day2-employee-category.assets/image-20231101223922112.png)

This is an API of the above. In most cases, we make an apoitment that if the request is sent from the admin endpoint, we use `/admin`  as a prefix in the url, if the reqeust is sent from user side, we use `/user`  as a prefix.

## sql table design

![image-20231101224108511](./day2-employee-category.assets/image-20231101224108511.png)

This is a sql table design. We need to add specific limitations on some certain field, eg: id is primary key and auto increasing, username should be unique, status have a default value 1.

# Add employee development

## 1. preliminary devlopment

During design period, we should save all the API documents to YApi or some other similar tools. Open that YApi and check the api before writing the codes.

![image-20231101233205798](./day2-employee-category.assets/image-20231101233205798.png)

### controller layer

Why should we use a DTO? We often use a DTO when the data sent by front is quite different from the entity, or if the data is very simple and there is no need to validate the data, we can directly use parameters.

As the data we receive is json, we need to add a `RequestBody`, and add an `ApiOperation`  so that swagger can have a comment telling what does this function do.

Add an ApiOperation comment for swagger. It is better not write too much business related codes here in controller,  so here we let service to save the DTO instead of entity. Later we will transform the DTO into entity in service implementation and then call a mapping persistant layer to save it into database.

```java
@ApiOperation("add a new employee")
@PostMapping
public Result add(@RequestBody EmployeeDTO employeeDTO) {
    log.info("add a new employee: {}", employeeDTO);
    employeeService.save(employeeDTO);
    return Result.success();
}
```

### Service layer

It is feasible to use entity.setField(dto.getField), but use `BeanUtils.copyProperites(DTO, entity)`  will be much more covinient if most of the properties have the same name. Entity contains some field that does not exist in the DTO, which need us to independently set those values.

There is a constant folder in sky-pojo modules, it stores a lot of constant class. It is more of OOP to use class's properties and functions than hard code it.

As for the password, we cannot save the clear text password, here we use a `DigestUtils.md5DigestAsHex(password.getBytes())`  to generate a cryptographic password.

```java
public void save(EmployeeDTO employeeDTO) {
    Employee employee = new Employee();
    BeanUtils.copyProperties(employeeDTO, employee);
    employee.setStatus(StatusConstant.ENABLE);
    // set default password
    String password = DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes());
    employee.setPassword(password);

    employee.setCreateTime(LocalDateTime.now());
    employee.setUpdateTime(LocalDateTime.now());

    // TODO need to use context user id
    employee.setCreateUser(1L);
    employee.setUpdateUser(1L);
    employeeMapper.save(employee);
}
```

### persistant/mapper layer

Persistant layer. It is mybatis grammer, use `@Select`  or use a mapping xml to transform the function into sql sentences. From here, we can see the design idea of mybatis is assumed that backend programmer are familiar with the sql so he can write some high-performance sql.

```java
@Insert("insert into employee (name, username, password, phone, sex, id_number, create_time, update_time, create_user, update_user, status) values (#{name}, #{username}, #{password}, #{phone}, #{sex}, #{idNumber}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser}, #{status})")
void save(Employee employee);
```

### mybatis config

Mybatis configuration is set in `application.yml`. The most important setting is turn on the underscore to camel case mapping on.

```yml
mybatis:
  #mapper配置文件
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.sky.entity
  configuration:
    #开启驼峰命名
    map-underscore-to-camel-case: true
```

## 2. swagger & joint test & debug

There are 2 ways to test: 

- swagger/postman all by back engineers
- frontend and backend engineers joint test

After we add a new api, we can see it in swagger, click the debug button and type in the correct json format data, then we can have a test.

![image-20231102001941122](./day2-employee-category.assets/image-20231102001941122.png)

If we got an 401 response status code, that means we are not authorized, there is because we have an interceptor and block this request. Login first and put that jwt token on swagger global parameters settings.

![image-20231102002239230](./day2-employee-category.assets/image-20231102002239230.png)

Why is the param name called `token` ? It is also defined in the `application.yml`.

```yml
sky:
  jwt:
    # 设置jwt签名加密时使用的秘钥
    admin-secret-key: itcast
    # 设置jwt过期时间
    admin-ttl: 7200000
    # 设置前端传递过来的令牌名称
    admin-token-name: token
```

After the swagger test, use nginx to proxy front project and have joint test. Also we need a jwt token, front project automatically store the token through login and carry that info to the header in the following requests.

## 3. perfect the code

There are 2 place to perfect

1. duplicte key for user account will lead a 500 internal error
2. set the empId from threadLocal

### duplicate key in table

First let's try to insert a duplicate employee and we get an error message `java.sql.SQLIntegrityConstraintViolationException: Duplicate entry 'liukai048' for key 'employee.idx_username'`. Go to server-handler module, add a new exceptionHandler to it, use the same function name and Java will automatically overload  cos we pass in a different exception class which can be found in the error msg.

Extract the duplicate key and add an error msg, the msg is also in the common-constant module.

```java
@ExceptionHandler
public Result exceptionHandler(SQLIntegrityConstraintViolationException ex) {
    if (ex.getMessage().contains("Duplicate entry")) {
        String[] splits = ex.getMessage().split(" ");
        String duplicateKey = splits[2];
        String msg = duplicateKey + MessageConstant.ALREADY_EXISTS;
        log.error("database unique error: {}", msg);
        return Result.error(msg);
    }
    return Result.error(MessageConstant.UNKNOWN_ERROR);
}
```

### Jwt & LocalThread

![image-20231102010922840](./day2-employee-category.assets/image-20231102010922840.png)

The empId is set in the jwt token, each request will first go through interceptors and extract the jwt. If we can extract the empId from the jwt and save it to somewhere, and in the later service layer we can access it that will be perfect.

This technique is ThreadLocal. ThreadLocal actually is not a thread, it is a storage related to a thread, every function in that thread share the same variables. But thread outside cannot visit.

We can use this to verify that in a request, different layers get the same thread id: `log.info("current thread is: {}", Thread.currentThread());`

In sky-common-context module, we can new a `ThreadLocal<T>` type object, like `@Data` we give this boject the getter and setter methods. So this object will be stored in the current thread.

```java
package com.sky.context;
public class BaseContext {
  public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();
  public static void setCurrentId(Long id) {
      threadLocal.set(id);
  }
  public static Long getCurrentId() {
      return threadLocal.get();
  }
  public static void removeCurrentId() {
      threadLocal.remove();
  }
}
```

when to set the empId? Everytime we want to access data, we will pass a interceptor and during that time we extract the empId from JWT and store it into current thread.

```java
Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
Long empId = Long.valueOf(claims.get(JwtClaimsConstant.EMP_ID).toString());
log.info("当前员工id：{}", empId);
BaseContext.setCurrentId(empId);
```

# Page query devlopment

## 1. Primary devlopment

### controller

We tend to be simple and non-biz related, implement all the biz logic in the service level. Here we just assumed we had receive the correct value and return.

```java
@ApiOperation("query page of employee")
@GetMapping("/page")
public Result<PageResult> page(EmployeePageQueryDTO dto) {
    log.info("page query: {}", dto);
    PageResult pageResult = employeeService.pageQuery(dto);
    return Result.success(pageResult);
}
```

PageResult is defined by ourself, it is simpler than Page class provided by PageHelper. 

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult implements Serializable {
    private long total; //总记录数
    private List records; //当前页数据集合
}
```

### service

Firstly use the PageHelper set the page info with pageNum and pageSize, it will set a ThreadLocal var in current thread. Later it will use an interceptor to block the results and encapsulate it into Page object. 

The mapper in fact returns list of Employee entities, but here we can directly return `Page<Employee>` . 

At last, we will convert the Page object into our self-defined PageResult object and return.

```java
@Override
public PageResult pageQuery(EmployeePageQueryDTO dto) {
    // PageHelper will store a ThreadLocal var in the thread
    PageHelper.startPage(dto.getPage(), dto.getPageSize());
    Page<Employee> page = employeeMapper.pageQuery(dto);
    long total = page.getTotal();
    List<Employee> records = page.getResult();
    return new PageResult(total, records);
}
```

### mapper

Mybatis encourages wrting sql sentences, but when it is complicated like automatically joint according to certain conditions, using xml is prefered. (or may use mybatis plus?)

```java
Page<Employee> pageQuery(EmployeePageQueryDTO dto);
```

But how can spring know where the implementation is? It is also in the application.yml.

```yml
mybatis:
  #mapper配置文件
  mapper-locations: classpath:mapper/*.xml
```

The mapper xml is in the `resource`  folder.

```shell
sky-server/src/main/resources/mapper/EmployeeMapper.xml
```

The id should be the same as the interface function name, and the result type is Employee entity, Page object will automatically convert the list of entity into `Page<Employee>`.

Pagequery has one field called name which supports fuzzy query, we use a concat function in order to avoid sql injection. But why here we got an  `and`  keyword?

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="com.sky.mapper.EmployeeMapper">
    <select id="pageQuery" resultType="com.sky.entity.Employee">
        select * from employee
        <where>
            <if test="name != null and name != ''">
                and name like concat('%', #{name}, '%')
            </if>
        </where>
        order by create_time desc
    </select>
</mapper>
```

## 2. test

Through testing, we find that the time format is not correct. It should be in datetime format but actually returns an array.

## 3. improvement

one way is add an annotation on the datetime field on entity. This way is quite simple, but if there are too many pojos, it will be very heavy work.

```java
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
private LocalDateTime createTime;
```

Another way is once and for all. The idea is to use a converter, when a java obejct is serialized into json, call the converter to convert LocalDatetime object into certain format json string.

There is an `extendMessageConverters` method in `WebMvcConfigurationSupport` class, we write an override method to add a new HttpMessageConverter to the converter list and set this self-defined one as the first priority (put it in the first index).

```java
// sky-server/src/main/java/com/sky/config/WebMvcConfiguration.java
protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
    log.info("start to extend message converters");
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setObjectMapper(new JacksonObjectMapper());
    converters.add(0, converter);
}
```



# Enable/Disable employee

<img src="./day2-employee-category.assets/image-20231103113355271.png" alt="image-20231103113355271" style="zoom:50%;" />

## self try

Try to implement this function by myself before watching the tutorial videos. But I found several problems.

1. The reqeust method is POST, but there is only one parameter in query, so should I use the whole EmployeeDTO or just one param?
2. When i try to implement the service, I found that mybatis does not provide me with the getById & updateById method as mybatis plus, should I manually implement this?

```java
public void updateStatus(EmployeeDTO employeeDTO, Integer status) {
    Employee employee = employeeMapper.getById(employeeDTO.getId());
    employee.setStatus(status);
    employeeMapper.updateById(employee);
}
```

## develop

Using mybatis is the sql idea, simple sql can be directly hard coded after a `@Select/Insert` , complicated sql can use DSL provided by mybatis to joint the full sql sentences.

**Controller**

Look at the api document, the id is not in the json body, so it is a normal parameter and we can directly receive it, no need to add any annotations before this param.

```java
@ApiOperation("enable/disable an employee")
@PostMapping("/status/{status}")
public Result changeStatus(@PathVariable Integer status, Long id) {
    log.info("change employee {} status to {}", id, status);
    employeeService.updateStatus(id, status);
    return Result.success();
}
```

**service**

There 

```java
public void updateStatus(Long id, Integer status) {
    // update employee set status=? where id=?
    // If there is a builder annotation
    Employee employee = Employee.builder()
            .id(id)
            .status(status)
            .build();
    employeeMapper.update(employee);
```


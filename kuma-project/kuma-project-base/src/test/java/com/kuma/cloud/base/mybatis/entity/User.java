package com.kuma.cloud.base.mybatis.entity;

import java.io.Serializable;

/**
 * 测试用实体
 *
 * <p><b>必须实现 Serializable</b>：二级缓存默认 readOnly=false，
 * MyBatis 会对对象进行序列化/反序列化以保证缓存条目的线程安全拷贝。
 * 若不实现该接口，开启二级缓存后会在运行时抛出
 * {@code NotSerializableException}。
 */
public class User implements Serializable {

    private Long id;
    private String name;
    private Integer age;

    public User() {}

    public User(Long id, String name, Integer age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', age=" + age + '}';
    }
}

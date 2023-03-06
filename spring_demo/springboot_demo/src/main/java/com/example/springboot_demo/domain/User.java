package com.example.springboot_demo.domain;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
public class User implements Serializable {
    private Integer id;
    @NotBlank(message = "用户名不能为空")
    @Length(max = 15, message = "用户名长度不能超过15位")
    private String username;
    @Pattern(regexp = "^(?!\\d+$)(?![a-z]+$)(?![A-Z]+$)[\\da-zA-z]{6,16}$", message = "密码不符合规则")
    private String password;

    public User(String username, String password)
    {
        this.username = username;
        this.password = password;
    }
}

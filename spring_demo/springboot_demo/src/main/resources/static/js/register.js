function submitForm() {
    var username = document.getElementById("username");
    var password = document.getElementById("password");
    var reg = new RegExp('^(?!\\d+$)(?![a-z]+$)(?![A-Z]+$)[\\da-zA-z]{6,16}$')
    if (username.value == null || password.value == null || username.value.length === 0 ||
        password.value.length === 0) {
        alert("用户名或密码不能为空")
        return;
    } else if (username.value.length > 15) {
        alert("用户名长度不应超过15位")
        return;
    } else if (!reg.test(password.value)) {
        alert("密码长度应在6-16位，且至少包含两种字符")
        return;
    }
    axios({
        method: "post",
        url: "http://localhost:8080/FromZerotoExpert/Register",
        data:
            {
                userName: username.value,
                userPwd: password.value
            }
    }).then(function (res) {
        if (res.data.code == 0) {
            alert("注册成功，即将跳转到登陆页");
            setTimeout('Redirect()', 1000);
            username.value = null;
            password.value = null;
        } else {
            alert("注册失败，原因：" + res.data.message)
            password.value = null;
        }
    })
}
function Redirect() {
    window.location="http://localhost:8080/FromZerotoExpert/login.html";
    //TODO: 上线前要更改重定向的url
}
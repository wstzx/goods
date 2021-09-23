$(function () {

    //得到所有的错误信息
    $(".labelError").each(function () {
        showError($(this));
    });

    //切换注册按钮的图片
    $("#submit").hover(function () {
        //移入
        $("#submit").attr("src", "/goods/images/regist2.jpg");
    }, function () {
        //移除
        $("#submit").attr("src", "/goods/images/regist1.jpg");
    });

    //输入框得到焦点隐藏错误信息
    $(".input").focus(function () {
        var lable = $(this).attr("id") + "Error";   //通过输入框找到lable的id
        $("#" + lable).text("");                  //清空lable的内容
        showError($("#" + lable));                //隐藏错误的图片
    });

    //输入框失去焦点进行校验
    $(".input").blur(function () {
        var id = $(this).attr("id");
        var funName = id + "x()";
        eval(funName);  //可以把字符串当成js代码执行

    });

    //表单提交时进行校验
    $("#registForm").submit(function () {
        var bool = true;      //表示校验通过
        if (!loginnamex()) {
            bool = false;
        }
        if (!loginpassx()) {
            bool = false;
        }
        if (!reloginpassx()) {
            bool = false;
        }
        if (!emailx()) {
            bool = false;
        }
        if (!verifyCodex()) {
            bool = false;
        }

        return bool;
    });

});

//判断当前元素是否存在内容,不存在则隐藏
function showError(ele) {

    var text = ele.text();
    if (!text) {
        ele.css("display", "none");
    } else {
        ele.css("display", "");
    }

}

//登录名校验
function loginnamex() {
    var value = $("#loginname").val();
    var lable = $("#loginnameError");
    //非空校验
    if (!value) {
        lable.text("用户名不能为空!");
        showError(lable);
        return false;
    }
    //长度校验
    if (value.length < 3 || value.length > 10) {
        lable.text("用户名长度在3-10位!");
        showError(lable);
        return false;
    }

    //是否注册校验
    $.ajax({
        url: "/goods/userServlet",
        data: {
            method: "ajaxLoginName",
            loginname: value
        },
        type: "POST",
        dataType: "json",
        async: false,       //这里必须得写false,否则程序可能会跳过这里直接执行下面的true
        cache: false,
        success: function (response) {
            if (!response) {      //校验失败
                lable.text("该用户名已被注册!");
                showError(lable);
                return false;
            }
        }
    });


    return true;
}

//登录密码校验
function loginpassx() {
    var value = $("#loginpass").val();
    var lable = $("#loginpassError");
    //非空校验
    if (!value) {
        lable.text("密码不能为空!");
        showError(lable);
        return false;
    }
    //长度校验
    if (value.length < 3 || value.length > 10) {
        lable.text("密码长度在3-10位!");
        showError(lable);
        return false;
    }

    return true;
}

//确认密码校验
function reloginpassx() {
    var login = $("#loginpass").val();
    var relogin = $("#reloginpass").val();
    var lable = $("#reloginpassError");
    //非空校验
    if (!relogin) {
        lable.text("确认密码不能为空!")
        showError(lable);
        return false;
    }

    //密码不一致校验
    if (login.length != relogin.length || login != relogin) {
        lable.text("两次密码输入不一致!")
        showError(lable);
        return false;
    }
    return true;
}

//邮箱校验
function emailx() {
    var value = $("#email").val();
    var lable = $("#emailError");
    //非空校验
    if (!value) {
        lable.text("邮箱不能为空!");
        showError(lable);
        return false;
    }
    //邮箱格式校验
    if (!/^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/.test(value)) {
        lable.text("Email格式不正确!");
        showError(lable);
        return false;
    }

    //是否注册校验

    $.ajax({
        url: "/goods/userServlet",
        data: {
            method: "ajaxEmail",
            email: value
        },
        type: "POST",
        dataType: "json",
        async: false,       //这里必须得写false,否则程序可能会跳过这里直接执行下面的true
        cache: false,
        success: function (response) {
            if (!response) {      //校验失败
                lable.text("该邮箱已被注册!");
                showError(lable);
                return false;
            }
        }
    });

    return true;
}

//验证码校验
function verifyCodex() {
    var value = $("#verifyCode").val();
    var lable = $("#verifyCodeError");
    //非空校验
    if (!value) {
        lable.text("验证码不能为空!")
        showError(lable);
        return false;
    }

    //长度校验
    if (value.length != 4) {
        lable.text("验证码错误!");
        showError(lable);
        return false;
    }

    //后台比较
    $.ajax({
        url: "/goods/userServlet",
        data: {
            method: "ajaxCode",
            verifyCode: value
        },
        type: "POST",
        dataType: "json",
        async: false,       //这里必须得写false,否则程序可能会跳过这里直接执行下面的true
        cache: false,
        success: function (response) {
            if (!response) {      //校验失败
                lable.text("验证码不正确!");
                showError(lable);
                return false;
            }
        }
    });

    return true;
}
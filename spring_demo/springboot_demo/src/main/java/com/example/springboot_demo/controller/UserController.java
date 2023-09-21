package com.example.springboot_demo.controller;

import cn.hutool.json.JSONUtil;
import com.example.springboot_demo.common.UserHolder;
import com.example.springboot_demo.common.utils.MyUtils;
import com.example.springboot_demo.common.utils.ResultUtil;
import com.example.springboot_demo.common.utils.SimpleRedisLock;
import com.example.springboot_demo.domain.User;
import com.example.springboot_demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.concurrent.TimeUnit;

import static com.example.springboot_demo.constants.RedisConstants.LOGIN_USER_KEY;
import static com.example.springboot_demo.constants.RedisConstants.LOGIN_USER_TTL;


@Controller
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    private final String format = "yyyy-MM-dd";

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @RequestMapping("/user/register")
    public ResultUtil<String> register(@RequestBody User user){
        String userName = user.getUserName();

        if (MyUtils.isIllegal(userName)) {
            return ResultUtil.build("您输入的用户名包含敏感词，请重新输入");
        }
        // 向数据库查询
        if (userService.isDuplicated(userName)) {
            return ResultUtil.build("用户名重复");
        }
        // 使用Redis分布式锁
        SimpleRedisLock lock = new SimpleRedisLock(userName, stringRedisTemplate);
        boolean islock = lock.tryLock(1200);
        if(!islock)
        {
            //获取锁失败
            return ResultUtil.build("当前注册不可用");
        }
        String code = MyUtils.generateCode(user.getUserPassword());
        user.setUserPassword(code);
        userService.add(user);
        lock.unlock();
        return ResultUtil.buildSuccess("注册成功");
    }


    @RequestMapping("/user/login")
    public ResultUtil<String> login(@RequestBody User user) {
        String username = user.getUserName();
        String password = user.getUserPassword();
        if(userService.getUserID(username) == null)
        {
            return ResultUtil.build("用户不存在");
        }
        String newPassword = userService.getPassWord(username);
        boolean check = passwordEncoder.matches(password, newPassword);
        if(check)
        {
            String token = MyUtils.getGUID();
            String tokenKey = LOGIN_USER_KEY + token;
            stringRedisTemplate.opsForValue().set(tokenKey, JSONUtil.toJsonStr(user), LOGIN_USER_TTL, TimeUnit.DAYS);
//            stringRedisTemplate.opsForZSet().add("onlineUser", userService.getUserID(username) + "", System.currentTimeMillis());
//            stringRedisTemplate.opsForValue().set("userId:" + userService.getUserID(username), token, 1, TimeUnit.MINUTES);
            return ResultUtil.buildSuccess(token);
        }
        else {
            return ResultUtil.build("用户名或密码错误");
        }
    }

    @PostMapping("/user/logout")
    public ResultUtil<Boolean> logout(){
        UserHolder.removeUser();
        return ResultUtil.success;
    }

//    @GetMapping("/Init")
//    public R start(HttpServletRequest request, HttpServletResponse response) throws ParamIllegalException {
//        HttpSession session = request.getSession();
//        String sessionAttribute = (String) session.getAttribute("LoginStatus");
//        String userId = redisTemplate.opsForValue().get(sessionAttribute);
//        String userName = userService.getUserName(Integer.parseInt(userId));
//        //是否有在线用户数记录（在线用户数记录1分钟更新一次）
//        String onlineNum = redisTemplate.opsForValue().get("onlineNum");
//        //没有在线用户数，就统计并生成在线用户数
//        if (onlineNum == null) {
//            long now = System.currentTimeMillis();
//            //将1分钟内有操作的用户视为在线，统计在线用户数
//            onlineNum = redisTemplate.opsForZSet().count("onlineUser", now - 60000, now) + "";
//            redisTemplate.opsForValue().set("onlineNum", onlineNum, 1, TimeUnit.MINUTES);
//        }
//        LocalDate today = LocalDate.now();
//        LocalDate yesterday = today.minusDays(1);
//        String todayIp = getIp(today.toString(), null).getData().get("ip").toString();
//        String yesterdayIp = getIp(yesterday.toString(), null).getData().get("ip").toString();
//        String todayUv = getUv(today.toString(), null).getData().get("uv").toString();
//        String yesterdayUv = getUv(yesterday.toString(), null).getData().get("uv").toString();
//        String todayPv = getPv(today.toString(), null).getData().get("pv").toString();
//        String yesterdayPv = getPv(yesterday.toString(), null).getData().get("pv").toString();
//        return R.ok().put("onlineNum", onlineNum).put("userName", userName).put("todayIp",todayIp)
//                .put("yesterdayIp", yesterdayIp).put("todayUv", todayUv).put("yesterdayUv", yesterdayUv)
//                .put("todayPv", todayPv).put("yesterdayPv", yesterdayPv);
//    }
//
//
//    @GetMapping("/ip")
//    public R getIp(@RequestParam(value = "startDay", required = false) String startDay, @RequestParam(value = "endDay", required = false) String endDay) throws ParamIllegalException {
//        Long num = 0L;
//        //如果没有接收到参数
//        if (startDay == null && endDay == null) {
//            LocalDate date = LocalDate.now();
//            String ip = date + ":ip";
//            num = redisTemplate.opsForHyperLogLog().size(ip);
//            //两个参数都接收到
//        } else if (startDay != null && endDay != null) {
//            List<LocalDate> days = getDays(startDay, endDay);
//            for (LocalDate localDate : days) {
//                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
//                String date = dateTimeFormatter.format(localDate) + ":ip";
//                Long size = redisTemplate.opsForHyperLogLog().size(date);
//                num += size;
//            }
//        } else {
//            LocalDate start = LocalDate.parse(startDay == null ? endDay : startDay, DateTimeFormatter.ofPattern(format));
//            String date = start + ":ip";
//            num = redisTemplate.opsForHyperLogLog().size(date);
//        }
//        return R.ok().put("ip", num);
//    }
//
//    @GetMapping("/pv")
//    public R getPv(@RequestParam(value = "startDay", required = false) String startDay, @RequestParam(value = "endDay", required = false) String endDay) throws ParamIllegalException {
//        int num = 0;
//        if (startDay == null && endDay == null) {
//            LocalDate date = LocalDate.now();
//            String pv = date + ":pv";
//            String size = redisTemplate.opsForValue().get(pv);
//            num = Integer.parseInt(size == null ? 0 + "" : size);
//        } else if (startDay != null && endDay != null) {
//            List<LocalDate> days = getDays(startDay, endDay);
//
//            for (LocalDate localDate : days) {
//                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
//                String date = dateTimeFormatter.format(localDate) + ":pv";
//                String size = redisTemplate.opsForValue().get(date);
//                num += Integer.parseInt(size == null ? 0 + "" : size);
//            }
//        } else {
//            LocalDate start = LocalDate.parse(startDay == null ? endDay : startDay, DateTimeFormatter.ofPattern(format));
//            String date = start + ":pv";
//            String size = redisTemplate.opsForValue().get(date);
//            num = Integer.parseInt(size == null ? 0 + "" : size);
//        }
//        return R.ok().put("pv", num);
//    }
//
//
//    @GetMapping("/uv")
//    public R getUv(@RequestParam(value = "startDay", required = false) String startDay, @RequestParam(value = "endDay", required = false) String endDay) throws ParamIllegalException {
//        Long num = 0L;
//        if (startDay == null && endDay == null) {
//            LocalDate date = LocalDate.now();
//            String uv = date + ":uv";
//            num = redisTemplate.opsForHyperLogLog().size(uv);
//        } else if (startDay != null && endDay != null) {
//            List<LocalDate> days = null;
//            days = getDays(startDay, endDay);
//            for (LocalDate localDate : days) {
//                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
//                String date = dateTimeFormatter.format(localDate) + ":uv";
//                Long size = redisTemplate.opsForHyperLogLog().size(date);
//                num += size;
//            }
//        } else {
//            LocalDate start = LocalDate.parse(startDay == null ? endDay : startDay, DateTimeFormatter.ofPattern(format));
//            String date = start + ":uv";
//            num = redisTemplate.opsForHyperLogLog().size(date);
//        }
//        return R.ok().put("uv", num);
//    }
//
//    private List<LocalDate> getDays(String startDay, String endDay) throws ParamIllegalException {
//        LocalDate end = LocalDate.parse(endDay, DateTimeFormatter.ofPattern(format));
//        LocalDate start = LocalDate.parse(startDay, DateTimeFormatter.ofPattern(format));
//        if (start.isAfter(end)) {
//            throw new ParamIllegalException();
//        }
//        long numOfDays = ChronoUnit.DAYS.between(start, end);
//
//        return Stream.iterate(start, date -> date.plusDays(1))
//                .limit(numOfDays)
//                .collect(Collectors.toList());
//    }
}


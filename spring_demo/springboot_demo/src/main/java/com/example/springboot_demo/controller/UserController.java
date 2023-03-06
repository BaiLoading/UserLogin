package com.example.springboot_demo.controller;

import com.example.springboot_demo.common.R;
import com.example.springboot_demo.common.utils.MyUtils;
import com.example.springboot_demo.common.utils.SimpleRedisLock;
import com.example.springboot_demo.domain.User;
import com.example.springboot_demo.exception.ParamIllegalException;
import com.example.springboot_demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;



@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    UserService userService;
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private StringRedisTemplate stringRedisTemplate;
    private final String format = "yyyy-MM-dd";

    @ResponseBody
    @PostMapping("/Register")
    public R register(@Valid User user){
        String userName = user.getUsername();

        if (MyUtils.isIllegal(userName)) {
            return R.error("您输入的用户名包含敏感词，请重新输入");
        }
        // 向数据库查询
//        if (userService.isDuplicated(userName)) {
//            return R.error("用户名重复");
//        }
        // 使用Redis分布式锁
        SimpleRedisLock lock = new SimpleRedisLock(userName, stringRedisTemplate);
        boolean islock = lock.tryLock(1200);
        if(!islock)
        {
            //获取锁失败
            return R.error("用户名重复");
        }
        String code = MyUtils.generateCode(user.getPassword());
        user.setPassword(code);
        userService.add(user);
        lock.unlock();
        return R.ok();
    }

    @ResponseBody
    @PostMapping("/Login")
    public R login(@Valid User user, HttpServletRequest request) {
        String username = user.getUsername();
        String password = user.getPassword();
        if(userService.getUserID(username)==null)
        {
            return R.error("用户不存在");
        }
        String newPassword = userService.getPassWord(username);
        boolean check = passwordEncoder.matches(password, newPassword);
        if(check)
        {
            HttpSession session = request.getSession();
            String token = MyUtils.getGUID();
            session.setAttribute("LoginStatus", token);
            session.setMaxInactiveInterval(24 * 60 * 60);
            redisTemplate.opsForValue().set(token, userService.getUserID(username) + "", 1, TimeUnit.DAYS);
            redisTemplate.opsForZSet().add("onlineUser", userService.getUserID(username) + "", System.currentTimeMillis());
            redisTemplate.opsForValue().set("userId:" + userService.getUserID(username), token, 1, TimeUnit.MINUTES);
            return R.ok();
        }
        else {
            return R.error("用户名或密码错误");
        }
    }

    @GetMapping("/Init")
    public R start(HttpServletRequest request, HttpServletResponse response) throws ParamIllegalException {
        HttpSession session = request.getSession();
        String sessionAttribute = (String) session.getAttribute("LoginStatus");
        String userId = redisTemplate.opsForValue().get(sessionAttribute);
        String userName = userService.getUserName(Integer.parseInt(userId));
        //是否有在线用户数记录（在线用户数记录1分钟更新一次）
        String onlineNum = redisTemplate.opsForValue().get("onlineNum");
        //没有在线用户数，就统计并生成在线用户数
        if (onlineNum == null) {
            long now = System.currentTimeMillis();
            //将1分钟内有操作的用户视为在线，统计在线用户数
            onlineNum = redisTemplate.opsForZSet().count("onlineUser", now - 60000, now) + "";
            redisTemplate.opsForValue().set("onlineNum", onlineNum, 1, TimeUnit.MINUTES);
        }
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        String todayIp = getIp(today.toString(), null).getData().get("ip").toString();
        String yesterdayIp = getIp(yesterday.toString(), null).getData().get("ip").toString();
        String todayUv = getUv(today.toString(), null).getData().get("uv").toString();
        String yesterdayUv = getUv(yesterday.toString(), null).getData().get("uv").toString();
        String todayPv = getPv(today.toString(), null).getData().get("pv").toString();
        String yesterdayPv = getPv(yesterday.toString(), null).getData().get("pv").toString();
        return R.ok().put("onlineNum", onlineNum).put("userName", userName).put("todayIp",todayIp)
                .put("yesterdayIp", yesterdayIp).put("todayUv", todayUv).put("yesterdayUv", yesterdayUv)
                .put("todayPv", todayPv).put("yesterdayPv", yesterdayPv);
    }


    @GetMapping("/ip")
    public R getIp(@RequestParam(value = "startDay", required = false) String startDay, @RequestParam(value = "endDay", required = false) String endDay) throws ParamIllegalException {
        Long num = 0L;
        //如果没有接收到参数
        if (startDay == null && endDay == null) {
            LocalDate date = LocalDate.now();
            String ip = date + ":ip";
            num = redisTemplate.opsForHyperLogLog().size(ip);
            //两个参数都接收到
        } else if (startDay != null && endDay != null) {
            List<LocalDate> days = getDays(startDay, endDay);
            for (LocalDate localDate : days) {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
                String date = dateTimeFormatter.format(localDate) + ":ip";
                Long size = redisTemplate.opsForHyperLogLog().size(date);
                num += size;
            }
        } else {
            LocalDate start = LocalDate.parse(startDay == null ? endDay : startDay, DateTimeFormatter.ofPattern(format));
            String date = start + ":ip";
            num = redisTemplate.opsForHyperLogLog().size(date);
        }
        return R.ok().put("ip", num);
    }

    @GetMapping("/pv")
    public R getPv(@RequestParam(value = "startDay", required = false) String startDay, @RequestParam(value = "endDay", required = false) String endDay) throws ParamIllegalException {
        int num = 0;
        if (startDay == null && endDay == null) {
            LocalDate date = LocalDate.now();
            String pv = date + ":pv";
            String size = redisTemplate.opsForValue().get(pv);
            num = Integer.parseInt(size == null ? 0 + "" : size);
        } else if (startDay != null && endDay != null) {
            List<LocalDate> days = getDays(startDay, endDay);

            for (LocalDate localDate : days) {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
                String date = dateTimeFormatter.format(localDate) + ":pv";
                String size = redisTemplate.opsForValue().get(date);
                num += Integer.parseInt(size == null ? 0 + "" : size);
            }
        } else {
            LocalDate start = LocalDate.parse(startDay == null ? endDay : startDay, DateTimeFormatter.ofPattern(format));
            String date = start + ":pv";
            String size = redisTemplate.opsForValue().get(date);
            num = Integer.parseInt(size == null ? 0 + "" : size);
        }
        return R.ok().put("pv", num);
    }


    @GetMapping("/uv")
    public R getUv(@RequestParam(value = "startDay", required = false) String startDay, @RequestParam(value = "endDay", required = false) String endDay) throws ParamIllegalException {
        Long num = 0L;
        if (startDay == null && endDay == null) {
            LocalDate date = LocalDate.now();
            String uv = date + ":uv";
            num = redisTemplate.opsForHyperLogLog().size(uv);
        } else if (startDay != null && endDay != null) {
            List<LocalDate> days = null;
            days = getDays(startDay, endDay);
            for (LocalDate localDate : days) {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
                String date = dateTimeFormatter.format(localDate) + ":uv";
                Long size = redisTemplate.opsForHyperLogLog().size(date);
                num += size;
            }
        } else {
            LocalDate start = LocalDate.parse(startDay == null ? endDay : startDay, DateTimeFormatter.ofPattern(format));
            String date = start + ":uv";
            num = redisTemplate.opsForHyperLogLog().size(date);
        }
        return R.ok().put("uv", num);
    }

    private List<LocalDate> getDays(String startDay, String endDay) throws ParamIllegalException {
        LocalDate end = LocalDate.parse(endDay, DateTimeFormatter.ofPattern(format));
        LocalDate start = LocalDate.parse(startDay, DateTimeFormatter.ofPattern(format));
        if (start.isAfter(end)) {
            throw new ParamIllegalException();
        }
        long numOfDays = ChronoUnit.DAYS.between(start, end);

        return Stream.iterate(start, date -> date.plusDays(1))
                .limit(numOfDays)
                .collect(Collectors.toList());
    }
}


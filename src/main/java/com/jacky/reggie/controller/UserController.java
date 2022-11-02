package com.jacky.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jacky.reggie.common.R;
import com.jacky.reggie.entity.User;
import com.jacky.reggie.service.UserService;
import com.jacky.reggie.utils.SMSUtils;
import com.jacky.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 发送手机验证码短信
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        String phone = user.getPhone();
        if(StringUtils.isNotEmpty(phone)) {
            String code = ValidateCodeUtils.generateValidateCode(6).toString();
            log.info("验证码code={}", code);

            //这里不用真的发送验证码
            //SMSUtils.sendMessage("瑞吉外卖", "", phone, code);

            //session.setAttribute(phone, code);
            redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);

            return R.success("手机验证码发送成功");
        }

        return R.error("短信发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();

        //Object codeInSession = session.getAttribute(phone);
        Object codeInSession = redisTemplate.opsForValue().get(phone);

        if(codeInSession != null && codeInSession.equals(code)) {
            //判断当前手机号对应的用户是否为新用户，如果是，则自动完成注册
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);
            if(user == null) {
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }

            session.setAttribute("user", user.getId());
            // 登录成功，删除验证码
            redisTemplate.delete(phone);

            return R.success(user);
        }

        return R.error("登录失败,请检查手机号和验证码");
    }

    @PostMapping("/loginout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("user");
        return R.success("退出成功Quit successfully");
    }
}

package com.gec.myparking.controller;

import com.gec.myparking.domain.LoginTicket;
import com.gec.myparking.domain.User;
import com.gec.myparking.service.LoginTicketService;
import com.gec.myparking.service.UserService;
import com.gec.myparking.util.Constant;
import com.gec.myparking.util.MyparkingUtil;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * todo Bug:添加用户时不上传图片提交成功没数据
 */
@Controller
@RequestMapping("/user")
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;

    @Autowired
    LoginTicketService loginTicketService;

    //访问用户页面
    @RequestMapping(value = "/userPage",method = RequestMethod.GET)
    public String userPage()
    {
        return "user/userList";
    }

    //访问添加用户页面
    @RequestMapping(value = "/addUserPage",method = RequestMethod.GET)
    public String addUserPage()
    {
        return "user/userAdd";
    }



    //todo 加入nickName
    @RequestMapping(value = "/addUser" ,method = RequestMethod.POST)
    @ResponseBody
    public  String addUser(@RequestParam String userName,
                           @RequestParam String email,
                           @RequestParam String nickName,
                           @RequestParam String password,
                           @RequestParam String headUrl
                           )
    //todo 方法添加headURL
    {
        try {
            Map map = userService.addUser(userName,password,email,headUrl,nickName);
            if (map.isEmpty())
                return MyparkingUtil.getJsonString(Constant.RESULT_STATUS_SUCCESS,"添加用户成功");
            else
                return MyparkingUtil.getJsonString(Constant.RESULT_STATUS_FAIL,map,"添加用户失败");
        }catch (Exception e)
        {
            LOGGER.error(e.getMessage());
            return MyparkingUtil.getJsonString(Constant.RESULT_STATUS_FAIL,"发生异常，添加用户失败");
        }
    }

    @RequestMapping(value = "/dologin",method = RequestMethod.POST)
    @ResponseBody
    public  String login(@RequestParam("username") String username,
                         @RequestParam("password") String password,
                         HttpServletResponse response)
    {
       try {
           Map<String,Object> map = new HashMap<>();

           map = userService.doLogin(username,password);

           if (map.containsKey("ticket"))
           {
               //将ticket保存到客户端以此做票据
               LoginTicket ticket = (LoginTicket) map.get("ticket");
               Cookie cookie = new Cookie("ticket", ticket.getTicket());
               cookie.setMaxAge(3600*24*1); //默认保留一天（单位为s）
               cookie.setPath("/");  //全网有效
               response.addCookie(cookie);

               return MyparkingUtil.getJsonString(Constant.RESULT_STATUS_SUCCESS,map,"登录成功");
           }
           return  MyparkingUtil.getJsonString(Constant.RESULT_STATUS_FAIL,map,"登录失败"); //登录失败，返回错误信息
       }catch (Exception e)
       {
           LOGGER.error(e.getMessage());
           return  MyparkingUtil.getJsonString(Constant.RESULT_STATUS_FAIL,"发生异常,登录失败");
       }
    }




//    @RequestMapping(value = "/register",method = RequestMethod.POST)
//    @ResponseBody
//    public  String reg(@RequestParam("username") String username,
//                       @RequestParam("password") String password,
//                       HttpServletResponse response)
//    {
//      try {
//          Map<String,Object> map = new HashMap<>();
//
//          map = userService.register(username,password);
//
//          if (map.containsKey("ticket"))
//          {
//              //将ticket保存到客户端以此做票据
//              LoginTicket ticket = (LoginTicket) map.get("ticket");
//              Cookie cookie = new Cookie("ticket", ticket.getTicket());
//              cookie.setPath("/");  //全网有效
//              response.addCookie(cookie);
//
//              return MyparkingUtil.getJsonString(Constant.RESULT_STATUS_SUCCESS,map,"注册成功");
//          }
//          return  MyparkingUtil.getJsonString(Constant.RESULT_STATUS_FAIL,map,"注册失败"); //注册失败，返回错误信息
//      }catch (Exception e )
//      {
//          LOGGER.error(e.getMessage());
//          return MyparkingUtil.getJsonString(Constant.RESULT_STATUS_FAIL,"发生异常，注册失败");
//      }
//    }

    @RequestMapping("/logout")
    @ResponseBody
    public String logout(@CookieValue("ticket") String ticket)
    {
        try {
            loginTicketService.doLoginOut(ticket);
            LOGGER.info("---------------------注销"+ticket+"成功---------------------");
            return MyparkingUtil.getJsonString(Constant.RESULT_STATUS_SUCCESS,"注销成功");
        }catch (Exception e )
        {
            LOGGER.error(e.getMessage());
            return MyparkingUtil.getJsonString(Constant.RESULT_STATUS_FAIL,"发生异常，注销失败");
        }

    }

    @RequestMapping(value = "/users",method = RequestMethod.GET)
    @ResponseBody
    public String getUsers(@RequestParam(value = "page",required = false) Integer page,
                           @RequestParam(value = "limit",required = false) Integer limit)
    {
        try {
             PageInfo<User> userPageInfo= userService.getUserList(page,limit);
             Map<String,Object> map = new HashMap<>();
             map.put("count",userPageInfo.getTotal());
             map.put("data",userPageInfo.getList());
             return MyparkingUtil.getJsonString(Constant.RESULT_STATUS_SUCCESS,map,"获取用户列表成功");
        }catch (Exception e )
        {
            LOGGER.error(e.getMessage());
            return MyparkingUtil.getJsonString(Constant.RESULT_STATUS_FAIL,"发生异常，获取用户列表失败");
        }
    }


    @RequestMapping(value = "/users",method = RequestMethod.DELETE)
    @ResponseBody
    public String deleteUser(@RequestParam(value = "id") Integer id)
    {
        try {
            userService.deleteUser(id);
            return MyparkingUtil.getJsonString(Constant.RESULT_STATUS_SUCCESS,"删除成功");
        }catch (Exception e)
        {
            LOGGER.error(e.getMessage());
            return  MyparkingUtil.getJsonString(Constant.RESULT_STATUS_FAIL,"发生异常，删除失败");
        }
    }


}

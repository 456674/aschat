import com.asyou20.aschat.dao.UserDao;
import com.asyou20.aschat.entity.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class testmaybatis {



    public static void main(String[] args) throws IOException {


        String  resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserDao userDao = sqlSession.getMapper(UserDao.class);

      //User user =new User(3,"2","2","2");
      //int s= userDao.insertUser(user);
       //sqlSession.commit();
        User user2 = new User();

        user2 = userDao.getUserById(2);
        // System.out.println(s);
        sqlSession.close();
    }
}

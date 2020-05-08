package cn.ymotel.dactor.action.mybatis;

import cn.ymotel.dactor.action.Actor;
import cn.ymotel.dactor.message.Message;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SqlQueryListActor implements Actor {
    private String sql;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
    private SqlSession sqlSession;
    public SqlSession getSqlSession() {
        return sqlSession;
    }

    public void setSqlSession(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }
    @Override
    public Object Execute(Message message) throws Exception {
        List ls= this.getSqlSession().selectList(sql,getParams(message));
        if(ls==null){
            ls=new ArrayList();
        }
        return ls;
    }
    public Map getParams(Message message){

        return message.getContext();
    }
}

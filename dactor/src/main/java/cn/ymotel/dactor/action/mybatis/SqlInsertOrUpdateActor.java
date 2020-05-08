package cn.ymotel.dactor.action.mybatis;

import cn.ymotel.dactor.action.Actor;
import cn.ymotel.dactor.message.Message;
import cn.ymotel.dactor.sequence.IdWorker;
import org.apache.ibatis.session.SqlSession;

import java.util.HashMap;
import java.util.Map;

public class SqlInsertOrUpdateActor implements Actor {
    private String insertsql;
    private String updatesql;
    private SqlSession sqlSession;

    public String getInsertsql() {
        return insertsql;
    }

    public void setInsertsql(String insertsql) {
        this.insertsql = insertsql;
    }

    public String getUpdatesql() {
        return updatesql;
    }

    public void setUpdatesql(String updatesql) {
        this.updatesql = updatesql;
    }

    public SqlSession getSqlSession() {
        return sqlSession;
    }

    public void setSqlSession(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public Object Execute(Message message) throws Throwable {
        Map map=getParams(message);
        int updatecount=this.getSqlSession().update(updatesql,map);
        if(updatecount==0) {
            map.put("_seq", IdWorker.getInstance().nextId());
            this.getSqlSession().insert(insertsql,map);
        }
        return new HashMap<>();
    }
    public Map getParams(Message message){

        return message.getContext();
    }
}

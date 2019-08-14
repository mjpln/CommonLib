package com.knowology.Bean;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 *内容摘要：业务节点类，主要存储KM系统中的基本业务节点信息
 *
 *类修改者：
 *修改说明：
 *@ClassName：ServiceNode
 *@Company：knowology
 *@Author：zhanggang
 *@Date：2013-06-20 14：42
 *@Version: V1.0
 */
public class ServiceNode
{
    public String Servcie = "";
    public String ServiceID = "";
    public Map<String, ServiceNode> Children = new HashMap<String, ServiceNode>();
    public ServiceNode Parent = null;
    public Map<String, ServiceNode> Ansestry = new HashMap<String, ServiceNode>();//所有的父结点
    /**
    *方法名称：ServiceNode
    *内容摘要：对业务节点进行初始化赋值
    *修改者：
    *修改说明：
    *@Author：zhanggang
    *@Param：_service,_serviceID
    *@Return：
    *@Throws：
    *
    */
    public ServiceNode(String _service, String _serviceID)
    {
        Servcie = _service;
        ServiceID = _serviceID;
    }
    /**
    *方法名称：ServiceNode
    *内容摘要：对业务节点进行初始化赋值
    *修改者：
    *修改说明：
    *@Author：zhanggang
    *@Param：_service,_serviceID,_parent
    *@Return：
    *@Throws：
    *
    */
    public ServiceNode(String _service, String _serviceID, ServiceNode _parent)
    {
        Servcie = _service;
        ServiceID = _serviceID;
        Parent = _parent;
        for (Entry<String, ServiceNode> iter : _parent.Ansestry.entrySet())
        {
            Ansestry.put(iter.getKey(), iter.getValue());
        }
        if (!Ansestry.containsKey(_parent.Servcie))
        {
            Ansestry.put(_parent.Servcie, _parent);
        }
    }
}

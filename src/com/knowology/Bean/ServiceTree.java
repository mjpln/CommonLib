package com.knowology.Bean;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.Map.Entry;

import javax.servlet.jsp.jstl.sql.Result;

import com.knowology.Log;
import com.knowology.DbDAO.Database4NLPapp;
import com.str.NewEquals;


    /**
     *内容摘要：业务树类，主要存储KM系统中的业务树结构信息
     *
     *类修改者：
     *修改说明：
     *@ClassName：ServiceTree
     *@Company：knowology
     *@Author：zhanggang
     *@Date：2013-06-20 14：42
     *@Version: V1.0
     */
    public class ServiceTree
    {
        public static ServiceNode s_Root = new ServiceNode("电信", "0");
        public static Boolean IsInitial = false;
        public static  Log myLog=Log.getLoger();
        public static Map<String, ArrayList<ServiceNode>> s_AllNodes = new HashMap<String, ArrayList<ServiceNode>>();

        /**
        *方法名称：GetAllBottomNode
        *内容摘要：获取业务树中所有的叶子节点
        *修改者：
        *修改说明：
        *@Author：zhanggang
        *@Param：
        *@Return：List
        *@Throws：
        *
        */
        public static ArrayList<String> GetAllBottomNode()
        {
        	ArrayList<String> res = new ArrayList<String>();
            for (Entry<String, ArrayList<ServiceNode>> iter : s_AllNodes.entrySet())
            {
                if (iter.getValue().size()!=0)
                {
                    for (ServiceNode sn : iter.getValue())
                    {
                        if (sn.Children.size() == 0)
                        {
                            res.add(iter.getKey());
                            break;
                        }
                    }
                }
            }
            return res;
        }
        /**
        *方法名称：CreateServiceTree
        *内容摘要：根据根节点构建业务树
        *修改者：
        *修改说明：
        *@Author：zhanggang
        *@Param：
        *@Return：
        *@Throws：
        *
        */
        public static void CreateServiceTree()
        {
            if (IsInitial) return;
            CreateServiceTree(s_Root);
            IsInitial = true;
        }
        
        /**
        *方法名称：CreateServiceTree
        *内容摘要：根据当前节点构建期对应的业务子树
        *修改者：
        *修改说明：
        *@Author：zhanggang
        *@Param：node
        *@Return：
        *@Throws：
        *
        */
        static void CreateServiceTree(ServiceNode node)
        {
        	ArrayList<ServiceNode> children = GetChildrenFromDB(node);
            if (children == null || children.size() == 0) return;
            for (ServiceNode ch : children)
            {
                if (!node.Children.containsKey(ch.Servcie))
                    node.Children.put(ch.Servcie, ch);
                CreateServiceTree(ch);
            }
        }
        
        /**
        *方法名称：GetChildrenFromDB
        *内容摘要：根据当前节点从数据库中获取其所有的孩子节点
        *修改者：
        *修改说明：
        *@Author：zhanggang
        *@Param：node
        *@Return：List
        *@Throws：
        *
        */
        static ArrayList<ServiceNode> GetChildrenFromDB(ServiceNode node)
        {
        	ArrayList<ServiceNode> children = new ArrayList<ServiceNode>();
        	String sql = "select * from service where parentid = " + node.ServiceID;
            try
            {
            Result dt = Database4NLPapp.executeQuery(sql);
            if(dt==null||dt.getRowCount()==0)return children;
            for (SortedMap<String,String> row : dt.getRows())
            {
                String service = row.get("service").toString();
                String serviceid =((Object)row.get("serviceid")).toString();
                if (serviceid == node.ServiceID)
                    continue;
                ServiceNode ch = new ServiceNode(service, serviceid, node);
                if (s_AllNodes.containsKey(ch.Servcie))
                {
                    s_AllNodes.get(ch.Servcie).add(ch);
                }
                else
                {
                	ArrayList<ServiceNode> lst = new ArrayList<ServiceNode>();
                    lst.add(ch);
                    s_AllNodes.put(ch.Servcie, lst);
                }
                children.add(ch);
            }
            return children;
            }catch (SQLException e) {
    			myLog.error(e.toString());
    			e.printStackTrace();
    		    return null;
    		}
        }

        @Deprecated
        /**
         * 
         *描述：获取孩子
         *@author: qianlei
         *@date： 日期：2015-3-17 时间：上午10:30:16
         *@return Map<String,ServiceNode>
         */
        public static Map<String, ServiceNode> GetChildren(ServiceNode node)
        {
            return new HashMap<String, ServiceNode>();
        }

        @Deprecated
        /**
         * 
         *描述：获取双亲
         *@author: qianlei
         *@date： 日期：2015-3-17 时间：上午10:30:32
         *@return Map<String,ServiceNode>
         */
        public static Map<String, ServiceNode> GetParents(ServiceNode node)
        {
            return new HashMap<String, ServiceNode>();
        }
        
        @Deprecated
        /**
         * 
         *描述：输出业务树
         *@author: qianlei
         *@date： 日期：2015-3-17 时间：上午10:31:00
         *@return void
         */
        public static void PrintServiceTree()
        { }
        
        /**
         * 
         *描述：获取双亲的名字
         *@author: qianlei
         *@date： 日期：2015-3-17 时间：上午10:32:36
         *@return java.util.ArrayList<String>
         */
		public static java.util.ArrayList<String> GetParentsName(String child, String serID)
		{
			java.util.ArrayList <String> result = new java.util.ArrayList<String>();
			if (s_AllNodes.containsKey(child))
			{
				for (ServiceNode sn : s_AllNodes.get(child))
				{
//					if (!serID.equals("") && !serID.equals(sn.ServiceID))//NewEquals.equals
					if (!serID.equals("") && !NewEquals.equals(serID,sn.ServiceID))
					{
						continue;
					}
					ServiceNode iterSn = sn;
					while (iterSn.Parent != null)
					{
						result.add(iterSn.Parent.Servcie);
						iterSn = iterSn.Parent;
					}
				}
			}
			return result;
		}
        
		/** 
		 获取符合以某字符串结尾的最近父亲节点
		 @param ser
		 @param str
		 @return 
		*/
		public static String GetNearestMeetedParent(String ser, String opt, String str)
		{
			java.util.ArrayList<String> parents = GetParentsName(ser, "");
			if (parents.size() > 0)
			{
				for (String ss : parents)
				{
					if (opt.equals("EndWith"))
					{
						if (ss.endsWith(str))
						{
							return ss;
						}
					}
					else if (opt.equals("Contain"))
					{
						if (ss.endsWith(str))
						{
							return ss;
						}
					}
				}
			}
			return "";
		}
        
        /**
        *方法名称：IsParent
        *内容摘要：检查当前孩子节点是否为父亲节点的孩子
        *修改者：
        *修改说明：
        *@Author：zhanggang
        *@Param：child,parent
        *@Return：bool
        *@Throws：
        *
        */
        public static Boolean IsParent(String child, String parent)
        {
            if (s_AllNodes.containsKey(child))
            {
                for (ServiceNode sn : s_AllNodes.get(child))
                {
                    if (sn.Ansestry.containsKey(parent))
                        return true;
                }
            }
            return false;
        }
        /**
        *方法名称：IsParent
        *内容摘要：检查当前孩子节点是否为父亲节点的孩子
        *修改者：
        *修改说明：
        *@Author：zhanggang
        *@Param：child,parent,parentid
        *@Return：bool
        *@Throws：
        *
        */
        public static Boolean IsParent(String child, String parent, String parentid)
        {
            if (s_AllNodes.containsKey(child))
            {
                for (ServiceNode sn : s_AllNodes.get(child))
                {
                    if (sn.Ansestry.containsKey(parent) && sn.Ansestry.get(parent).ServiceID == parentid)
                    {
                        return true;
                    }
                }
            }
            return false;
        }
        /**
        *方法名称：IsBottomChild
        *内容摘要：检查当前节点是否为叶子节点
        *修改者：
        *修改说明：
        *@Author：zhanggang
        *@Param：node
        *@Return：bool
        *@Throws：
        *
        */
        public static Boolean IsBottomChild(String node)
        {
            if (s_AllNodes.containsKey(node))
            {
                for (ServiceNode sn : s_AllNodes.get(node))
                {
                    if (sn.Children.size() == 0)
                        return true;
                }

            }
            return false;
        }
        /**
        *方法名称：Updata
        *内容摘要：更新业务树的所有节点信息
        *修改者：
        *修改说明：
        *@Author：zhanggang
        *@Param：
        *@Return：
        *@Throws：
        *
        */
        public static void Updata()
        {
            s_Root = new ServiceNode("电信", "0");
            s_AllNodes = new HashMap<String, ArrayList<ServiceNode>>();
            System.gc();
            CreateServiceTree();
        }
 }

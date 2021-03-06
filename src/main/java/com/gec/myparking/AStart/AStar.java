package com.gec.myparking.AStart;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * 
 * ClassName: AStar 
 * @Description: A星算法
 * @author peter
 */
public class AStar
{
	public final static int BAR = 1; // 障碍值
	public final static int EMPTY = 0; // 道路可通行
	public final static int PATH = 4; // 路径
	public final static int DIRECT_VALUE = 10; // 横竖移动代价
	public final static int OBLIQUE_VALUE = 14; // 斜移动代价
	public final static List<Coord> resultCoordList = new ArrayList();
	
	Queue<Node> openList = new PriorityQueue<Node>(); // 优先队列(升序)
	List<Node> closeList = new ArrayList<Node>();
	
	/**
	 * 开始算法
	 */
	public void start(MapInfo mapInfo)
	{
		if(mapInfo==null) return;
		// clean
		openList.clear();
		closeList.clear();
		// 开始搜索
		openList.add(mapInfo.start);
		moveNodes(mapInfo);
	}

	/**
	 * 移动当前结点
	 */
	private void moveNodes(MapInfo mapInfo)
	{
		while (!openList.isEmpty())
		{
			if (isCoordInClose(mapInfo.end.coord))
			{
				//绘制路径
				drawPath(mapInfo.maps, mapInfo.end);
				break;
			}
			Node current = openList.poll();
			closeList.add(current);
			addAllNeighborNodeInOpen(mapInfo,current);
		}
	}
	
	/**
	 * 在二维数组中绘制路径
	 */
	private void drawPath(int[][] maps, Node end)
	{
		List<Coord> resultCoordList = getResultCoordList();
		//清空resultCoordList中的元素
		resultCoordList.clear();
		if(end==null||maps==null) return;
		//System.out.println("总代价：" + end.G);
		while (end != null)
		{
			Coord c = end.coord;
			//保存每个结果坐标到集合中
			resultCoordList.add(c);
			maps[c.y][c.x] = PATH;
			end = end.parent;
		}




	}

	/**
	 * 添当前点所有邻结点到open表
	 */
	private void addAllNeighborNodeInOpen(MapInfo mapInfo, Node current)
	{
		int x = current.coord.x;
		int y = current.coord.y;
		// 左
		addAnNeighborNodeInOpen(mapInfo,current, x - 1, y, DIRECT_VALUE);
		// 上
		addAnNeighborNodeInOpen(mapInfo,current, x, y - 1, DIRECT_VALUE);
		// 右
		addAnNeighborNodeInOpen(mapInfo,current, x + 1, y, DIRECT_VALUE);
		// 下
		addAnNeighborNodeInOpen(mapInfo,current, x, y + 1, DIRECT_VALUE);
		// 左上
		addAnNeighborNodeInOpen(mapInfo,current, x - 1, y - 1, OBLIQUE_VALUE);
		// 右上
		addAnNeighborNodeInOpen(mapInfo,current, x + 1, y - 1, OBLIQUE_VALUE);
		// 右下
		addAnNeighborNodeInOpen(mapInfo,current, x + 1, y + 1, OBLIQUE_VALUE);
		// 左下
		addAnNeighborNodeInOpen(mapInfo,current, x - 1, y + 1, OBLIQUE_VALUE);
	}

	/**
	 * 添加一个邻结点到open表
	 */
	private void addAnNeighborNodeInOpen(MapInfo mapInfo, Node current, int x, int y, int value)
	{
		if (canAddNodeToOpen(mapInfo,x, y))
		{
			Node end=mapInfo.end;
			Coord coord = new Coord(x, y);
			int G = current.G + value; // 计算邻结点的G值
			Node child = findNodeInOpen(coord);
			if (child == null)
			{
				int H=calcH(end.coord,coord); // 计算H值
				if(isEndNode(end.coord,coord))
				{
					child=end;
					child.parent=current;
					child.G=G;
					child.H=H;
				}
				else
				{
					child = new Node(coord, current, G, H);
				}
				openList.add(child);
			}
			else if (child.G > G)
			{
				child.G = G;
				child.parent = current;
				openList.add(child);
			}
		}
	}

	/**
	 * 从Open列表中查找结点
	 */
	private Node findNodeInOpen(Coord coord)
	{
		if (coord == null || openList.isEmpty()) return null;
		for (Node node : openList)
		{
			if (node.coord.equals(coord))
			{
				return node;
			}
		}
		return null;
	}


	/**
	 * 计算H的估值：“曼哈顿”法，坐标分别取差值相加
	 */
	private int calcH(Coord end,Coord coord)
	{
		return Math.abs(end.x - coord.x)
				+ Math.abs(end.y - coord.y);
	}
	
	/**
	 * 判断结点是否是最终结点
	 */
	private boolean isEndNode(Coord end,Coord coord)
	{
		return coord != null && end.equals(coord);
	}

	/**
	 * 判断结点能否放入Open列表
	 */
	private boolean canAddNodeToOpen(MapInfo mapInfo,int x, int y)
	{
		// 是否在地图中
		if (x < 0 || x >= mapInfo.width || y < 0 || y >= mapInfo.hight) return false;
		// 判断是否是不可通过的结点
		if (mapInfo.maps[y][x] == BAR) return false;
		// 判断结点是否存在close表
		if (isCoordInClose(x, y)) return false;

		return true;
	}

	/**
	 * 判断坐标是否在close表中
	 */
	private boolean isCoordInClose(Coord coord)
	{
		return coord!=null&&isCoordInClose(coord.x, coord.y);
	}

	/**
	 * 判断坐标是否在close表中
	 */
	private boolean isCoordInClose(int x, int y)
	{
		if (closeList.isEmpty()) return false;
		for (Node node : closeList)
		{
			if (node.coord.x == x && node.coord.y == y)
			{
				return true;
			}
		}
		return false;
	}

	public static List getResultCoordList() {
		return resultCoordList;
	}
}

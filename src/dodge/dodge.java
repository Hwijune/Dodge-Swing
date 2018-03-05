package block;

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.sql.*;

public class dodge extends JFrame implements Runnable

{
	Container c;
	
	MenuBar mb;
	Menu game, help, rankings;
	MenuItem start, end, helpItem, ranking;
	static Connection conn = null;
	static String Query = null;
	static PreparedStatement pstmt = null;
	static ResultSet rs = null;
	static String rid;
	static int n;
	Dialog di, helpDi;
	static Dialog rankDi;
	static Label diLabel1;
	static Label diLabel2;
	Label diLabel3;
	TextField field;
	String name;
	Button btn;
	Thread th = new Thread(this);
	ship ship1;
	ArrayList<onebullet> bullet = new ArrayList<onebullet>();
	
	JLabel gameover = new JLabel("GAME OVER!!");
	JLabel count = new JLabel(Integer.toString(n));
	JLabel bullnum = new JLabel("총알 갯수 : " + Integer.toString(bullet.size()));
	
	Actionli acli = new Actionli();
	KeyLi keli = new KeyLi();
	class Actionli implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() == end) // 메뉴의 선택을 통한 종료
			{
				setVisible(false);
				dispose();
				System.exit(0);
			} else if (e.getSource() == start) // 메뉴의 선택을 통한 시작
			{
				
				th.start();
				System.out.println("시작됬냐");
			} else if ((e.getSource() == btn) || (e.getSource() == field)) // 다이얼로그창에서
																			// 버튼
																			// 클릭
																			// 및
																			// 엔터키
																			// 입력
			{
				di.hide();
				name = field.getText();
				rankingInsert(name, n);
			} else if (e.getSource() == helpItem) // 메뉴에서 도움말을 선택해서 보여주는 부분
			{
				helpDi.show();
			}
			
			else if (e.getSource() == ranking) // 메뉴에서 도움말을 선택해서 보여주는 부분
			{
				
				rankDi.removeAll();
				
				Test();
				rankDi.show();
			}
		}
		
	}
	
	class KeyLi implements KeyListener
	{
		public void keyPressed(KeyEvent arg0)
		{
			if (arg0.getKeyCode() == KeyEvent.VK_ENTER)
			{
				
				th.start();
				System.out.println("dd");
			}
			if (arg0.getKeyCode() == KeyEvent.VK_RIGHT)
			{
				ship1.setLocation(ship1.getX() + 5, ship1.getY());
			}
			if (arg0.getKeyCode() == KeyEvent.VK_LEFT)
			{
				ship1.setLocation(ship1.getX() - 5, ship1.getY());
			}
			if (arg0.getKeyCode() == KeyEvent.VK_UP)
			{
				ship1.setLocation(ship1.getX(), ship1.getY() - 5);
			}
			if (arg0.getKeyCode() == KeyEvent.VK_DOWN)
			{
				ship1.setLocation(ship1.getX(), ship1.getY() + 5);
			}
			
		}	
		
		public void keyReleased(KeyEvent arg0)
		{
			// TODO Auto-generated method stub
			
		}
		
		public void keyTyped(KeyEvent arg0)
		{
			// TODO Auto-generated method stub
			
		}
	}
	


	
	public void rankingInsert(String Rname, int Rscore)
	{ // 최고점수 갱신시 업데이트
		try
		{
			
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/r"
					+ "anking?characterEncoding=utf8&verifyServerCertificate=false&useSSL=true",
					"root", "rootpass");
			
			Query = "insert into ranking values(?,?)";
			
			pstmt = conn.prepareStatement(Query);
			pstmt.setString(1, Rname);
			pstmt.setInt(2, Rscore);
			pstmt.executeUpdate();
			
		} catch (ClassNotFoundException cnfe)
		{
			System.out.print("ㄴ" + cnfe.getMessage());
		} catch (SQLException se)
		{
			System.out.print("ㄴㄴ" + se.getMessage());
		}
	}
	
	public void Test()
	{
		try
		{
			
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/ranking?characterEncoding=utf8&verifyServerCertificate=false&useSSL=true",
					"root", "rootpass");
			
			Query = "select id, score from ranking where score>0 order by score desc limit 0,10";
			pstmt = conn.prepareStatement(Query);
			rs = pstmt.executeQuery();
			int rank = 1;
			String header[] = { "이름", "점수" };
			String contents[][] = new String[5][2];
			
			while (rs.next())
			{
				rid = rs.getString(1);
				n = rs.getInt(2);
				System.out.println(rid + " " + n);
				
				diLabel1 = new Label(rank + "위 // 이름 : " + rid);
				System.out.println(rid + n);
				rankDi.add(diLabel1);
				
				String strScore = String.valueOf(n);
				diLabel2 = new Label(" | 점수 : " + strScore + "점 ");
				rankDi.add(diLabel2);
				contents[rank - 1][0] = rid;
				contents[rank - 1][1] = Integer.toString(n);
				rank++;
				if (rank == 6)
				{
					break;
				}
			}
			
			JTable tb = new JTable(contents, header);
			JScrollPane scroll = new JScrollPane(tb);
			rankDi.add(scroll);
			
		} catch (ClassNotFoundException cnfe)
		{
			System.out.print("ㄴ" + cnfe.getMessage());
		} catch (SQLException se)
		{
			System.out.print("ㄴㄴ" + se.getMessage());
		}
	}
	
	dodge()
	{
		this.setTitle("dodge");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		mb = new MenuBar();
		setMenuBar(mb);
		
		game = new Menu("게임");
		mb.add(game);
		
		help = new Menu("도움말");
		mb.setHelpMenu(help);
		
		rankings = new Menu("랭킹");
		mb.add(rankings);
		
		start = new MenuItem("시작");
		start.addActionListener(acli);
		game.add(start);
		
		end = new MenuItem("종료");
		end.addActionListener(acli);
		game.add(end);
		
		helpItem = new MenuItem("도움말");
		helpItem.addActionListener(acli);
		help.add(helpItem);
		
		ranking = new MenuItem("랭킹보기");
		ranking.addActionListener(acli);
		rankings.add(ranking);
		
		helpDi = new Dialog(this, "help", true); // 도움말 다이얼로그
		helpDi.setLayout(new FlowLayout());
		helpDi.addWindowListener(new WinDiaHandler());
		helpDi.resize(400, 100);
		
		diLabel1 = new Label("방향키 ( →:오른족, ←:왼쪽, ↑:회전, ↓:아래쪽 )");
		helpDi.add(diLabel1);
		diLabel2 = new Label("총알을 피하는 게임입니다. ");
		helpDi.add(diLabel2);
		
		rankDi = new Dialog(this, "Ranking", true); // 랭킹 다이얼로그
		rankDi.setLayout(new FlowLayout());
		rankDi.addWindowListener(new WinDiaHandler());
		rankDi.resize(500, 500);
		
		c = getContentPane();
		this.setContentPane(c);
		this.setSize(400, 450);
		this.setVisible(true);
		this.setResizable(false);
		
		this.setLayout(null);
		
		ship1 = new ship();
		
		gameover.setLocation(80, 80);
		gameover.setFont(new Font("gothic", 30, 30));
		gameover.setSize(200, 200);
		gameover.setOpaque(false);
		gameover.setForeground(Color.white);
		gameover.setVisible(false);
		
		count.setLocation(180, -70);
		count.setFont(new Font("gothic", 30, 30));
		count.setSize(200, 200);
		count.setOpaque(false);
		count.setForeground(Color.white);
		count.setVisible(true);
		
		bullnum.setLocation(240, 300);
		bullnum.setFont(new Font("gothic", 15, 15));
		bullnum.setSize(200, 100);
		bullnum.setOpaque(false);
		bullnum.setForeground(Color.white);
		bullnum.setVisible(true);
		
		this.add(bullnum);
		this.add(count);
		this.add(gameover);
		this.add(ship1);
		
		for (int i = 0; i < 50; i++)
		{
			onebullet p = new onebullet();
			
			int x = (int) (Math.random() * 200 + 100);
			int y = (int) (Math.random() * 200 + 100);
			
			if (i > 40)
				p.setLocation(x, 100);
			else if (i > 30)
				p.setLocation(100, y);
			else if (i > 20)
				p.setLocation(300, y);
			else if (i > 0)
				p.setLocation(x, 300);
			
			bullet.add(p);
			this.add(p);
		}
		
		this.addKeyListener(keli);
		this.setFocusable(true);
		this.getContentPane().setBackground(Color.black);
		
	}
	
	public void run()
	{
		int num = 50;
		int stacknum = 0;
		while (true)
		{
			try
			{
				Thread.sleep(num);
				stacknum += num;
				if (stacknum % 100 == 0)
				{
					n++;
					count.setText(Integer.toString(n));
				}
				bulletmove();
				checkdie();
				
				if (stacknum % 2000 == 0)
				{
					onebullet p = new onebullet();
					
					int x = (int) (Math.random() * 400);
					int y = (int) (Math.random() * 400);
					
					p.bx = (int) (Math.random() * 8 + 1);
					p.by = (int) (Math.random() * 8 + 1);
					p.dx = (int) (Math.random() * 8 + 1);
					p.dy = (int) (Math.random() * 8 + 1);
					
					p.setLocation(x, y);
					p.setBackground(Color.blue);
					
					bullet.add(p);
					this.add(p);
				}
				
				bullnum.setText("총알 갯수 : " + Integer.toString(bullet.size()));
			} catch (InterruptedException e)
			{
				
			}
			
		}
	}
	

	
	public void paintComponent(Graphics g)
	{
		super.paintComponents(g);
		
		g.setColor(Color.black);
		g.fillRect(0, 0, 400, 400);
	}
	
	public void bulletmove()
	{
		for (onebullet bb : bullet)
		{
			bb.setLocation(bb.getX() + bb.bx - bb.dx, bb.getY() + bb.by - bb.dy);
			
			if (bb.getX() > 400)
				bb.setLocation(0, bb.getY());
			if (bb.getY() > 400)
				bb.setLocation(bb.getX(), 0);
			if (bb.getX() < 0)
				bb.setLocation(400, bb.getY());
			if (bb.getY() < 0)
				bb.setLocation(bb.getX(), 400);
			
		}
	}
	
	class ship extends JLabel
	{
		ImageIcon ic = new ImageIcon("images/ship.png");
		
		ship()
		{

			this.setIcon(ic);
			this.setLocation(150, 150);
			this.setOpaque(false);
			this.setSize(15, 15);
		}
	}
	
	class onebullet extends JLabel
	{
		int bx = (int) (Math.random() * 5 + 1);
		int by = (int) (Math.random() * 5 + 1);
		int dx = (int) (Math.random() * 5 + 1);
		int dy = (int) (Math.random() * 5 + 1);
		
		onebullet()
		{
			if (bx == dx)
				bx = (int) (Math.random() * 5 + 1);
			if (by == dy)
				by = (int) (Math.random() * 5 + 1);
			
			this.setSize(3, 3);
			this.setBackground(Color.yellow);
			this.setOpaque(true);
		}
	}
	
	public boolean contains(JLabel p, int x, int y) // 포함되는지 확인
	{
		if (((p.getX() <= x) && (x < p.getX() + p.getWidth())) && (p.getY() < y) && (y < p.getY() + p.getHeight()))
		{
			// System.out.println(ball.x + " , " + ball.y);
			return true;
		} else
			return false;
	}
	
	public void checkdie()
	{
		for (onebullet bb : bullet)
		{
			if (contains(bb, ship1.getX(), ship1.getY()) || contains(bb, ship1.getX() + ship1.getWidth(), ship1.getY())
					|| contains(bb, ship1.getX(), ship1.getY() + ship1.getHeight())
					|| contains(bb, ship1.getX() + ship1.getWidth(), ship1.getY() + ship1.getHeight()))
			{
				gameover.setVisible(true);
				System.out.println("d");
				int cou = 0;
				while (true)
				{
					di = new Dialog(this, "축하합니다.", true); // 점수등록 다이얼로그 생성
					di.setLayout(new FlowLayout());
					di.add(new Label("이름:"));
					field = new TextField("", 10);
					field.addActionListener(acli);
					btn = new Button("등록");
					btn.addActionListener(acli);
					di.add(field);
					di.add(new Label(Integer.toString(n)));
					di.add(btn);
					di.addWindowListener(new WinDiaHandler());
					di.resize(300, 100);
					di.show();
					System.out.println("dd");
					cou++;
					
					if (cou == 1)
					{
						break;
					}
				}
				th.stop();
				th = null;
				
			}
			
			if (contains(ship1, bb.getX(), bb.getY()) || contains(ship1, bb.getX() + bb.getWidth(), bb.getY())
					|| contains(ship1, bb.getX(), bb.getY() + bb.getHeight())
					|| contains(ship1, bb.getX() + bb.getWidth(), bb.getY() + bb.getHeight()))
			{
				gameover.setVisible(true);
				System.out.println("d");
				
				int cou = 0;
				while (true)
				{
					di = new Dialog(this, "축하합니다.", true); // 점수등록 다이얼로그 생성
					di.setLayout(new FlowLayout());
					di.add(new Label("이름:"));
					field = new TextField("", 10);
					field.addActionListener(acli);
					btn = new Button("등록");
					btn.addActionListener(acli);
					di.add(field);
					di.add(new Label(Integer.toString(n)));
					di.add(btn);
					di.addWindowListener(new WinDiaHandler());
					di.resize(300, 100);
					di.show();
					System.out.println("dd");
					cou++;
					
					if (cou == 1)
					{
						break;
					}
				}
				th.stop();
				th = null;
				
			}
		}
		
	}
	
	class WinDiaHandler extends WindowAdapter // 다이얼로그 닫기 버튼 이벤트 핸들러
	{
		public void windowClosing(WindowEvent e)
		{
			Window w = e.getWindow();
			w.setVisible(false);
			w.dispose();
		}
	}
	
	public static void main(String[] args)
	{
		new dodge();
	}
	
}

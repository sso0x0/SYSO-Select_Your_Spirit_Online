package frame;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import DAO.ItemDAO;
import DTO.Item;
import DTO.MyData;

public class FrameItemSelect extends JPanel {

	public FrameItemSelect(MyData d) {

		setBackground(new Color(181, 227, 216));
		setLayout(null);
		setSize(600, 2000);

		ImageIcon liquor1 = new ImageIcon("Img/00.png");
		JButton btnLiq1 = new JButton(liquor1);
		btnLiq1.setName("발베니 12년");
		btnLiq1.setSize(250, 250);
		btnLiq1.setLocation(30, 10);
		add(btnLiq1);

		btnLiq1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				Item liquor1 = new ItemDAO().SearchItem(16);
				FrameBase.getInstance(new FrameItemInfo(liquor1, d));
			}
		});

		setBackground(new Color(181, 227, 216));
		setLayout(null);
		setSize(600, 2000);

		ImageIcon liquor2 = new ImageIcon("Img/01.png");
		JButton btnLiq2 = new JButton(liquor2);
		btnLiq2.setName("글랜피딕 15년");
		btnLiq2.setSize(250, 250);
		btnLiq2.setLocation(300, 10);
		add(btnLiq2);

		btnLiq2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				Item liquor2 = new ItemDAO().SearchItem(10);
				FrameBase.getInstance(new FrameItemInfo(liquor2, d));
			}
		});

		// 하단의 버튼(뒤로가기)
		JPanel bottomSet = new JPanel();
		bottomSet.setBounds(0, 660, 600, 100);
		bottomSet.setLayout(null);
		bottomSet.setBackground(new Color(0xffd700));

		JButton btnBack = new JButton("뒤로가기");
		btnBack.setBackground(new Color(0xa6a6a6));
		btnBack.setSize(183, 87);
		btnBack.setLocation(5, 0);
		btnBack.setFont(new Font("나눔고딕코딩", Font.BOLD, 22));

		bottomSet.add(btnBack);

		btnBack.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				FrameBase.getInstance(new FrameBegin());

			}
		});
	}
}

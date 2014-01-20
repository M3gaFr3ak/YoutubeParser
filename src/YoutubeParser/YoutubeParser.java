package YoutubeParser;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class YoutubeParser extends JFrame implements ActionListener
{
	JTextField urlField = new JTextField();
	JButton startButton = new JButton("Parse URL!");
	JList list = new JList();

	public YoutubeParser()
	{
		setTitle("YoutubeParser");
		setSize(600, 800);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		startButton.addActionListener(this);
		getContentPane().add(new JScrollPane(list), BorderLayout.CENTER);
		getContentPane().add(urlField, BorderLayout.NORTH);
		getContentPane().add(startButton, BorderLayout.SOUTH);
		list.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent me)
			{
				Object ob = list.getSelectedValue();
				System.out.println("sda");
				if (ob == null)
					return;
				if (me.getClickCount() == 2)
				{
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(new StringSelection((String) ob), null);
					me.consume();
				}
			}
		});
		pack();
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		try
		{
			List<YoutubeUtil.Video> links = YoutubeUtil.getLinks(urlField.getText());
			String[] urls = new String[links.size()];
			for (int i = 0; i < urls.length; i++)
			{
				urls[i] = links.get(i).url;
			}
			list.setListData(urls);
		} catch (Throwable e)
		{
			JDialog dialog = new JDialog(this, "Invalid URL!", true);
			dialog.setVisible(true);
		}
	}
}

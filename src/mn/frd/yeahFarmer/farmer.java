package mn.frd.yeahFarmer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;



public final class farmer extends JavaPlugin implements Listener {
	@Override
	public void onEnable(){
		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println("[cmd v" + pdfFile.getVersion() + "] sucessfully enabled!");

		getServer().getPluginManager().registerEvents(new farmerPlayerListener(this), this);

		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println("[cmd v" + pdfFile.getVersion() + "] sucessfully disabled!");
	}

	public boolean isInteger( String input )  
	{  
		try  
		{  
			Integer.parseInt( input );  
			return true;  
		}  
		catch( Exception e)  
		{  
			return false;  
		}  
	}  
	public Boolean isRunning = false;
	public Boolean isConfirmed = false;
	public Integer money;
	public String timeinmin;
	public String currentuser;

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {






		if (!(sender instanceof Player))
		{
			return false;
		}

		final Player p = (Player) sender;

		if (cmd.getName().equalsIgnoreCase("farmer"))
		{
			if (p.hasPermission("farmer.use")) {

				// Botusernamen vergeben
				String bot = getConfig().getString("botusername");
				Player botuser = Bukkit.getPlayer(bot);

				// Pruefen ob Bot online ist.
				if (getServer().getOfflinePlayer(bot).isOnline()){
					// Pruefen ob Argumente angegeben wurden
					if (args.length == 0){
						// Kein Argument
						p.sendMessage(ChatColor.GRAY + "[Farmer]" + ChatColor.GOLD + " No argument given. Example: " + ChatColor.GREEN + "/farmer 10" + ChatColor.GOLD + " to rent the farmer for 10 minutes." );
					} else if (args.length > 1){
						// Mehr als ein Argument uebergeben
						p.sendMessage(ChatColor.GRAY + "[Farmer]" + ChatColor.GOLD + " Too many arguments :(");
					} else {
						// Pruefen ob eine Task bereits laeuft
						if(isRunning){ 
							// Pruefen ob der User nicht der Owner des aktuellen Tasks ist.
							if (!p.getName().equalsIgnoreCase(currentuser)) {
								// Wenn der "nicht-Owner" mit yes bestaetigt
								if (args[0].equalsIgnoreCase("yes")) {
									p.sendMessage(ChatColor.GRAY + "[Farmer]" + ChatColor.RED + " You are not " + currentuser);
									return true;
								} else {
									// Ansonsten
									p.sendMessage(ChatColor.GRAY + "[Farmer] " + ChatColor.GOLD + " The farmer is currently working for " + currentuser + ".");
									return true;
								}

							}
						}


						// Pruefen ob Bestätigung erscheinen soll 
						if(isConfirmed){
							// Pruefen ob Bestaetigung mit yes erfolgte
							if (args[0].equalsIgnoreCase("yes")) {


								isConfirmed = false;

								p.sendMessage(ChatColor.GRAY + "[Farmer]" + ChatColor.GREEN + " You are sure. Transfering money...");

								Integer ticks = Integer.parseInt( timeinmin ) * 1200;  
								long tickstring = ticks;

								getServer().dispatchCommand(getServer().getConsoleSender(), "money take " + p.getName() + " " + money.toString());

								botuser.teleport(p.getLocation());
								p.sendMessage(ChatColor.GRAY + "[Farmer]" + ChatColor.GOLD + " Here is your farmer for exact " + timeinmin + " minutes.");
								this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
									@Override 
									public void run() {
										p.sendMessage(ChatColor.GRAY + "[Farmer]" + ChatColor.GOLD + " My time is over. See you later.");
										getServer().dispatchCommand(getServer().getConsoleSender(), "warp " + getConfig().getString("warpname") + getConfig().getString("botuserxname"));
										isRunning = false;
									}
								}, tickstring);
								return true;
							} else {
								//Prüfung erfolgte nicht mit yes -> Abbrechen und freigeben
								isRunning = false;
								isConfirmed = false;
								p.sendMessage(ChatColor.GRAY + "[Farmer]" + ChatColor.RED + " Well, nevermind then.");
								return true;
							}

						} // ifConfirmed prüfung ende

						// Pruefen ob das Argument eine Zahl ist
						if (isInteger(args[0])) {

							isRunning = true;

							// Ist eine Zahl
							timeinmin = args[0];
							money = Integer.parseInt( timeinmin ) * getConfig().getInt("price-per-minute");  
							currentuser = p.getName();  

							p.sendMessage(ChatColor.GRAY + "[Farmer]" + ChatColor.GOLD + " This will cost " + money.toString() + " Dollars. If you are sure type "+ ChatColor.AQUA + "/farmer yes" + ChatColor.GOLD + ".");
							isConfirmed = true;

						} else {
							// Keine zahl
							p.sendMessage(ChatColor.GRAY + "[Farmer]" + ChatColor.GOLD + " Not a valid time format :(");
						}
					}
				} else {
					// Kein Bot online 
					p.sendMessage(ChatColor.GRAY + "[Farmer]" + ChatColor.GOLD + " Farmer not available. I try to get him here. Please enter your command again in 10 seconds.");
					String result = "";
					{  try 
					{  

						URL url = new URL("http://static.yeahwh.at:80/.pycraft/?p=iPZwn41d"); 

						URLConnection connection = url.openConnection(); 	 


						BufferedReader in = new BufferedReader(new 
								InputStreamReader(connection.getInputStream())); 

						String line;
						while ((line = in.readLine()) != null) 
						{  System.out.println(line); 
						result = result + line; 
						}	        
					} 
					catch (IOException exception) 
					{  System.out.println("Fehler: " + exception); 
					} 
					}   
				}

				return true;
			}
			return false;
		}
		return true;
	}
}
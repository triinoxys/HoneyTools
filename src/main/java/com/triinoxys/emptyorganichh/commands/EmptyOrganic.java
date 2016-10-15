package com.triinoxys.emptyorganichh.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.triinoxys.emptyorganichh.Cuboid;
import com.triinoxys.emptyorganichh.Cut;
import com.triinoxys.emptyorganichh.Empty;
import com.triinoxys.emptyorganichh.ListBlock;
import com.triinoxys.emptyorganichh.Replace;
import com.triinoxys.emptyorganichh.Set;
import com.triinoxys.emptyorganichh.Undo;

public class EmptyOrganic implements CommandExecutor, TabCompleter{
    
    public static String prefix = "�7[�aEmptyOrganic�7] �r";
    private String help = prefix + "�c/emptyorganic <tool | empty | clear | pos1 | pos2 | undo | replace | cut | set>";
	
    public static List<Player> players = new ArrayList<Player>();
    
    public static HashMap<Player, Location> l1 = new HashMap<Player, Location>();
    public static HashMap<Player, Location> l2 = new HashMap<Player, Location>();
	
	public static void clearSelection(Player p) {
		if (l1.containsKey(p)) l1.remove(p);
		if (l2.containsKey(p)) l2.remove(p);
	}
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player){
		    Player p = (Player) sender;
		    
		    if(p.isOp() || p.hasPermission("emptyorganic.*")){
		        if(args.length > 0){
		            if(!p.isOp()){
		                p.sendMessage(prefix + "�cVous n'avez pas la permission !");
		                return true;
		            }
		            
		            
		            if(args.length == 0){
		                p.sendMessage(help);
		                return true;
		            }
		            
		            
		            // Tool
		            if(args[0].equalsIgnoreCase("tool")){
		                if(players.contains(p)){
		                    players.remove(p);
		                    p.sendMessage(prefix + "�6Vous venez de �cd�sactiver �6l'outil de s�lection.");
		                }
		                else{
		                    players.add(p);
		                    p.sendMessage(prefix
		                            + "�6Vous venez d'�aactiver �6l'outil de s�lection.");
		                    ItemStack is = new ItemStack(Material.STICK);
		                    ItemMeta im = is.getItemMeta();
		                    im.setDisplayName("�aOutil de s�lection EmptyOrganic");
		                    im.setLore(Arrays.asList("�6Outil de s�lection,",
		                            "�6pour �aEmptyOrganic�6."));
		                    is.setItemMeta(im);
		                    p.getInventory().addItem(is);
		                    p.updateInventory();
		                }
		            }
		            
		            // Empty
		            else if(args[0].equalsIgnoreCase("empty")){
		                if(l1.containsKey(p) && l2.containsKey(p)){
		                    Cuboid cuboid = new Cuboid(l1.get(p), l2.get(p));
		                    ListBlock lb = new ListBlock();
		                    Empty empty = new Empty(p, cuboid, lb);
		                    empty.start();
		                }else{
		                    p.sendMessage(prefix
		                            + "�cMerci de faire une s�lection avant de faire cela !");
		                }
		            }
		            
		            // Clear
		            else if(args[0].equalsIgnoreCase("clear")){
		                clearSelection(p);
		                p.sendMessage(prefix + "�aS�lection clear.");
		            }
		            
		            // Pos1
		            else if(args[0].equalsIgnoreCase("pos1")){
		                if(l1.containsKey(p)) l1.remove(p);
		                l1.put(p, p.getLocation());
		                p.sendMessage(prefix + "�aPoint 1 cr�e");
		            }
		            
		            // Pos2
		            else if(args[0].equalsIgnoreCase("pos2")){
		                if(l2.containsKey(p)) l2.remove(p);
		                l2.put(p, p.getLocation());
		                p.sendMessage(prefix + "�aPoint 2 cr�e");
		            }
		            
		            // Undo
		            else if(args[0].equalsIgnoreCase("undo")){
		                if(Undo.undos.containsKey(p)){
		                    Undo.undos.get(p).undo();
		                    Undo.undos.remove(p);
		                    p.sendMessage(prefix + "�aUndo fait avec succ�s");
		                }else
		                    p.sendMessage(prefix + "�cAucun undo en cache");
		            }
		            
		            // Replace
		            else if(args[0].equalsIgnoreCase("replace")){
		                if(args.length != 3){
		                    p.sendMessage(prefix
		                            + "�c/emptyorganic replace <blocs � remplac�s (ex: 1,2:1)> <en ce bloc>");
		                }
		                if(!l1.containsKey(p) || !l2.containsKey(p)){
		                    p.sendMessage(prefix
		                            + "�cMerci de faire une s�lection avant de faire cela !");
		                }
		                Cuboid cuboid = new Cuboid(l1.get(p), l2.get(p));
		                ItemStack replaceBlock;
		                if(args[2].contains(":")){
		                    String[] idM = args[2].split(":");
		                    int id = Integer.parseInt(idM[0].replace(":", ""));
		                    byte meta = Byte.parseByte(idM[1].replace(":", ""));
		                    replaceBlock = new ItemStack(id, 1, meta);
		                }else{
		                    int id = Integer.parseInt(args[2]);
		                    replaceBlock = new ItemStack(id, 1);
		                }
		                List<ItemStack> items = new ArrayList<ItemStack>();
		                if(args[1].contains(",")){
		                    String[] blocks = args[1].split(",");
		                    for(String id : blocks){
		                        if(id.contains(":")){
		                            String[] idM = id.split(":");
		                            int idSM = Integer.parseInt(idM[0].replace(":", ""));
		                            byte meta = Byte.parseByte(idM[1].replace(":", ""));
		                            items.add(new ItemStack(idSM, 1, meta));
		                        }else{
		                            int idM = Integer.parseInt(id);
		                            items.add(new ItemStack(idM, 1));
		                        }
		                    }
		                    Replace replace = new Replace(cuboid, p, items, replaceBlock);
		                    replace.replace();
		                }else{
		                    if(args[1].contains(":")){
		                        String[] idM = args[1].split(":");
		                        int id = Integer.parseInt(idM[0].replace(":", ""));
		                        byte meta = Byte.parseByte(idM[1].replace(":", ""));
		                        items.add(new ItemStack(id, 1, meta));
		                    }else{
		                        int id = Integer.parseInt(args[1]);
		                        items.add(new ItemStack(id, 1));
		                    }
		                    Replace replace = new Replace(cuboid, p, items, replaceBlock);
		                    replace.replace();
		                }
		            }
		            
		            // Set
		            else if(args[0].equalsIgnoreCase("cut")){
		                if(l1.containsKey(p) && l2.containsKey(p)){
		                    Cuboid cuboid = new Cuboid(l1.get(p), l2.get(p));
		                    Cut cut = new Cut(cuboid, p);
		                    cut.cut();
		                }else{
		                    p.sendMessage(prefix
		                            + "�cMerci de faire une s�lection avant de faire cela !");
		                }
		            }
		            
		            // Set
		            else if(args[0].equalsIgnoreCase("set")){
		                if(args.length != 2){
		                    p.sendMessage(prefix + "�c/emptyorganic set <blocs � set>");
		                }
		                if(!l1.containsKey(p) || !l2.containsKey(p)){
		                    p.sendMessage(prefix
		                            + "�cMerci de faire une s�lection avant de faire cela !");
		                }
		                Cuboid cuboid = new Cuboid(l1.get(p), l2.get(p));
		                ItemStack is;
		                if(args[1].contains(":")){
		                    String[] idM = args[1].split(":");
		                    int id = Integer.parseInt(idM[0].replace(":", ""));
		                    byte meta = Byte.parseByte(idM[1].replace(":", ""));
		                    is = new ItemStack(id, 1, meta);
		                }else{
		                    int id = Integer.parseInt(args[1]);
		                    is = new ItemStack(id, 1);
		                }
		                Set set = new Set(cuboid, p, is);
		                set.set();
		            }
		            else{
		                p.sendMessage(help);
		            }
		        }
		        else p.sendMessage(help);
	        }
		    else p.sendMessage(prefix + "�cVous n'avez pas la permission !");
		    
		}
		else sender.sendMessage(ChatColor.DARK_RED + "Vous devez etre un joueur pour executer cette commande.");
		
        return true;
	}
	
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> argsList = new ArrayList<String>();
        argsList.add("on");
        argsList.add("off");
        
        if(args.length == 0) return argsList;
        else{
            List<String> pArgs = new ArrayList<String>();
            
            for(String arg : argsList)
                if(arg.toLowerCase().startsWith(args[0].toLowerCase()))
                    pArgs.add(arg);
            
            return pArgs;
        }
    }

}

package com.ForgeEssentials.snooper.response;

import java.util.Collection;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.api.json.JSONException;
import com.ForgeEssentials.api.json.JSONObject;
import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.snooper.Response;
import com.ForgeEssentials.api.snooper.TextFormatter;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.economy.WalletHandler;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

public class PlayerInfoResonce extends Response
{
	private boolean	sendhome;
	private boolean	sendpotions;
	private boolean	sendXP;
	private boolean	sendArmorAndHealth;
	private boolean	sendFood;
	private boolean	sendCapabilities;
	private boolean	sendMoney;
	private boolean	sendPosition;

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getResponce(JSONObject input) throws JSONException
	{
		JSONObject PlayerData = new JSONObject();
		JSONObject tempMap = new JSONObject();

		if (!input.has("username"))
			return new JSONObject().put(this.getName(), "This responce needs a username!");
		
		EntityPlayerMP player = server.getConfigurationManager().getPlayerForUsername(input.getString("username"));
		if (player == null)
			return new JSONObject().put(this.getName(), input.getString("username") + " not online!");

		PlayerInfo pi = PlayerInfo.getPlayerInfo(player.username);
		if (pi != null && sendhome)
		{
			if (pi.home != null)
			{
				PlayerData.put("Home", pi.home.toJSON());
			}
			if (pi.back != null)
			{
				PlayerData.put("Back", pi.back.toJSON());
			}
		}

		if (sendArmorAndHealth)
		{
			PlayerData.put("Armor", "" + player.inventory.getTotalArmorValue());
			PlayerData.put("Health", "" + player.getHealth());
		}
		try
		{
			if(sendMoney) PlayerData.put("Money", "" + WalletHandler.getWallet(player));
		}
		catch (Exception e)
		{
		}
		if(sendPosition) PlayerData.put("Position", new WorldPoint(player).toJSON());
		PlayerData.put("Ping", "" + player.ping);
		PlayerData.put("Gamemode", player.theItemInWorldManager.getGameType().getName());

		if (!player.getActivePotionEffects().isEmpty() && sendpotions)
		{
			PlayerData.put("Potions", TextFormatter.toJSON((Collection<PotionEffect>) player.getActivePotionEffects()));
		}

		if (sendXP)
		{
			tempMap = new JSONObject();
			tempMap.put("lvl", "" + player.experienceLevel);
			tempMap.put("bar", "" + player.experience);
			PlayerData.put("XP", tempMap);
		}

		if (sendFood)
		{
			tempMap = new JSONObject();
			tempMap.put("Food", "" + player.getFoodStats().getFoodLevel());
			tempMap.put("Saturation", "" + player.getFoodStats().getSaturationLevel());
			PlayerData.put("FoodStats", tempMap);
		}

		if (sendCapabilities)
		{
			tempMap = new JSONObject();
			tempMap.put("edit", "" + player.capabilities.allowEdit);
			tempMap.put("allowFly", "" + player.capabilities.allowFlying);
			tempMap.put("isFly", "" + player.capabilities.isFlying);
			tempMap.put("noDamage", "" + player.capabilities.disableDamage);
		}
		PlayerData.put("Capabilities", tempMap);

		try
		{
			PlayerData.put("group", PermissionsAPI.getHighestGroup(player).name);
		}
		catch (Exception e)
		{
		}
		
		PlayerData.put("firstJoin", PlayerInfo.getPlayerInfo(player.username).getFirstJoin());
		PlayerData.put("timePlayed", PlayerInfo.getPlayerInfo(player.username).timePlayed);
		
		return new JSONObject().put(this.getName(), PlayerData);
		
	}

	@Override
	public String getName()
	{
		return "PlayerInfoResonce";
	}

	@Override
	public void readConfig(String category, Configuration config)
	{
		sendhome = config.get(category, "sendHome", true).getBoolean(true);
		sendpotions = config.get(category, "sendpotions", true).getBoolean(true);
		sendXP = config.get(category, "sendXP", true).getBoolean(true);
		sendArmorAndHealth = config.get(category, "sendArmorAndHealth", true).getBoolean(true);
		sendFood = config.get(category, "sendFood", true).getBoolean(true);
		sendCapabilities = config.get(category, "sendCapabilities", true).getBoolean(true);
		sendMoney = config.get(category, "sendMoney", true).getBoolean(true);
		sendPosition = config.get(category, "sendPosition", true).getBoolean(true);
	}

	@Override
	public void writeConfig(String category, Configuration config)
	{
		config.get(category, "sendHome", true).value = "" + sendhome;
		config.get(category, "sendpotions", true).value = "" + sendpotions;
		config.get(category, "sendXP", true).value = "" + sendXP;
		config.get(category, "sendArmorAndHealth", true).value = "" + sendArmorAndHealth;
		config.get(category, "sendFood", true).value = "" + sendFood;
		config.get(category, "sendCapabilities", true).value = "" + sendCapabilities;
		config.get(category, "sendMoney", true).value = sendMoney + "";
		config.get(category, "sendPosition", true).value = sendPosition + "";
	}
}

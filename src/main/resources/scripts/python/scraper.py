import requests
from bs4 import BeautifulSoup
from sqlalchemy import create_engine, MetaData, Table, Column, Integer, String, ForeignKey, CheckConstraint
from sqlalchemy.orm import sessionmaker
import os
import time

BASE_URL = "https://www.futwiz.com"
IMG_PATH = "../../static/images"
nations_path = IMG_PATH + "/nations"
leagues_path = IMG_PATH + "/leagues"
teams_path = IMG_PATH + "/teams"
players_path = IMG_PATH + "/players"

league_mapping = {
    "ENG 1": "Premier League",
    "ESP 1": "LaLiga",
    "ITA 1": "Serie A",
    "GER 1": "Bundesliga",
    "FRA 1": "Ligue 1",
    "POR 1": "Liga Portugal",
    "TUR 1": "Süper Lig",
    "NED 1": "Eredivisie",
    "BEL 1": "Pro League",
    "GRE 1": "Superleague Elláda",
    "AUT 1": "Austrian Bundesliga",
    "UKR 1": "Premier-Liha",
    "SPFL": "Scottish Premiership",
    "SAU 1": "Saudi Pro League",
    "MLS": "Major League Soccer",
    "LIB": "Primera División",
    "SUD": "Primera División",
    "UEA 1": "UAE Pro League",
    "KOR 1": "K League 1",
    "ENG 2": "EFL Championship",
    "ITA 2": "Serie B",
    "ESP 2": "LaLiga 2"
}

img_league_url = "https://cdn.futwiz.com/assets/img/fc25/leagues"
img_league_mapping = {
    img_league_url + "/1003.png" : img_league_url + "/353.png",
    img_league_url + "/1014.png" :  img_league_url + "/353.png",
}

def create_directory_if_not_exists(folder_path):
    if not os.path.exists(folder_path):
        os.makedirs(folder_path)

def extract_player_details(player_url):
    url = BASE_URL + player_url
    response = requests.get(url)

    if response.status_code == 200:
        soup = BeautifulSoup(response.content, 'html.parser')

        details = {}

        team_row = soup.find('div', string="Club")
        if team_row:
            team_value = team_row.find_next_sibling('div').text.strip()
            details['team'] = team_value

        league_row = soup.find('div', string="League")
        if league_row:
            league_value = league_row.find_next_sibling('div').text.strip()
            league_value = league_mapping.get(league_value, league_value)
            details['league'] = league_value

        nationality_row = soup.find('div', string="Nationality")
        if nationality_row:
            nationality_value = nationality_row.find_next_sibling('div').text.strip()
            details['nationality'] = nationality_value

        return details
    else:
        print(f"Error accessing page {url}: {response.status_code}")
        return None

def download_image(image_url, folder_path, image_name):
    file_path = "/".join([folder_path, image_name])

    response = requests.get(image_url)
    if response.status_code == 200:
        with open(file_path, 'wb') as f:
            f.write(response.content)
    else:
        print(f"Error downloading the image from {image_url}: {response.status_code}")

def get_or_create(table, key_column, key_value, image_url, image_folder_path, **extra_fields):
    query = session.query(table).filter_by(**{key_column: key_value})
    instance = query.first()

    if instance:
        return instance.id
    else:
        ins = table.insert().values({key_column: key_value, **extra_fields})
        result = session.execute(ins)
        session.commit()
        id = result.inserted_primary_key[0]

        download_image(image_url, image_folder_path, f"{id}.png")

        return id

def insert_player(player_name, player_position, details, player_image_url, nation_image_url, league_image_url, team_image_url):
    nation_name = details.get('nationality', "")
    team_name = details.get('team', "")
    league_name = details.get('league', "")

    nation_id = get_or_create(nationality_table, 'name', nation_name, nation_image_url, nations_path)
    league_id = get_or_create(league_table, 'name', league_name, league_image_url, leagues_path)
    team_id = get_or_create(team_table, 'name', team_name, team_image_url, teams_path, league_id=league_id)

    ins = player_table.insert().values(
        name=player_name,
        position=player_position,
        nationality_id=nation_id,
        team_id=team_id
    )

    result = session.execute(ins)
    session.commit()
    player_id = result.inserted_primary_key[0]

    download_image(player_image_url, players_path, f"{player_id}.png")

def scrape_futwiz():
    create_directory_if_not_exists(nations_path)
    create_directory_if_not_exists(leagues_path)
    create_directory_if_not_exists(teams_path)
    create_directory_if_not_exists(players_path)

    for i in range(21):
        url = BASE_URL + f"/en/fc25/players?page={i}&release[]=raregold&sex=men"
        response = requests.get(url)

        if response.status_code == 200:
            soup = BeautifulSoup(response.content, 'html.parser')

            player_cards = soup.find_all('a', {'class': 'latest-player-card'})
            
            for card in player_cards:
                player_url = card['href']
                player_name = card.find('div', class_='card-25-pack-name').get_text().strip()
                player_position = card.find('div', class_='card-25-pack-position').get_text().strip()
                player_img_url = card.find('div', class_='card-25-pack-face-inner').find('img')['src']
                nation_img_url = card.find('div', class_='card-25-pack-nation').find('img')['src']
                league_img_url = card.find('div', class_='card-25-pack-league').find('img')['src']
                league_img_url = img_league_mapping.get(league_img_url, league_img_url)
                team_img_url = card.find('div', class_='card-25-pack-club').find('img')['src']
                
                details = extract_player_details(player_url)

                if details:
                    insert_player(player_name, player_position, details, player_img_url, nation_img_url, league_img_url, team_img_url)
                time.sleep(0.5)
        else:
            print(f"Error accessing page {url}: {response.status_code}")

    print("Scraping completed")

def check_image_existence(folder_path, image_name):
    file_path = "/".join([folder_path, image_name])

    if not os.path.exists(file_path):
        print(f"Image missing: {file_path}")

def check_images_existence_in_folders():
    nations = session.query(nationality_table).all()
    leagues = session.query(league_table).all()
    teams = session.query(team_table).all()
    players = session.query(player_table).all()


    for nation in nations:
        check_image_existence(nations_path, f"{nation.id}.png")

    for league in leagues:
        check_image_existence(leagues_path, f"{league.id}.png")

    for team in teams:
        check_image_existence(teams_path, f"{team.id}.png")

    for player in players:
        check_image_existence(players_path, f"{player.id}.png")

    print("Check completed")

if __name__ == "__main__":
    engine = create_engine('postgresql+psycopg2://root:root@localhost:5433/footcardz')
    Session = sessionmaker(bind=engine)

    with Session() as session:
        metadata = MetaData()
        
        nationality_table = Table(
            'nationality', metadata,
            Column('id', Integer, primary_key=True),
            Column('name', String, nullable=False, unique=True)
        )

        league_table = Table(
            'league', metadata,
            Column('id', Integer, primary_key=True),
            Column('name', String, nullable=False, unique=True)
        )

        team_table = Table(
            'team', metadata,
            Column('id', Integer, primary_key=True),
            Column('name', String, nullable=False),
            Column('league_id', Integer, ForeignKey('league.id'), nullable=False)
        )

        player_table = Table(
            'player', metadata,
            Column('id', Integer, primary_key=True),
            Column('name', String, nullable=False),
            Column('position', String, nullable=False),
            Column('nationality_id', Integer, ForeignKey('nationality.id'), nullable=False),
            Column('team_id', Integer, ForeignKey('team.id'), nullable=False),
            CheckConstraint("position IN ('ST', 'RW', 'LW', 'CAM', 'CM', 'CDM', 'RM', 'LM', 'CB', 'RB', 'LB', 'GK')", name='valid_position')
        )
                
        scrape_futwiz()
        check_images_existence_in_folders()
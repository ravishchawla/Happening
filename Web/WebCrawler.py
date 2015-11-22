import urllib3.request;
import json;
import xml.etree.ElementTree as etree;
import pymongo;
from pymongo import MongoClient;

cities = [];
app_key = 'zrKgdfNwMBtMJ2nV';
today = 'today';
url = 'http://api.eventful.com/rest/events/search';
method = 'GET';

def read_cities():
    cities = open("cities.list",'r').read().split('\n')
    print(cities);
    return cities;

cities = read_cities();
http = urllib3.PoolManager();

client = MongoClient();
dbase = client.happening;
events_collection = dbase.events;

for city in cities:
    request_url = url + "?app_key=" + app_key + "&date=" + today + "&city=" + city;
    print(request_url);
    request = http.request(method, request_url);

    tree = etree.ElementTree(etree.fromstring(request.data));
    root = tree.getroot();


    for event in root.iter('event'):
        data = {};
        

        
        title = event.find('title').text;  
        venue = event.find('venue_name').text;
        #print(venue);
        venue_address = event.find('venue_address').text;
        #print(venue_address);
        city_name = event.find('city_name').text;
        #print(city_name);
        state_name = event.find('region_name').text;
        #print(state_name);
        country = event.find('country_abbr').text;
        #print(country);
        zip_code = event.find('postal_code').text;
        #print(zip_code);

        datetime = event.find('start_time').text;
        
        
        description = event.find('description').text;

        image = event.find('image');
        if image is not None:
            small = image.find('small');
            if small is not None:
                image_url = small.find('url');
                
        
        


        if None in(venue, venue_address, city_name, state_name, country, zip_code, datetime, title):
            data = data;
        else:    
            date_time = datetime.split(' ');
            date = date_time[0];
            time = date_time[1];
            location = venue + " " + venue_address + ", " + city_name + ", " + state_name + ", " + country + ", " + zip_code;
            data = {'eventname': title, 'description': description, 'location':location, 'date': date, 'city': city_name, 'time': time};

          #  if url is not None:
           #     data['imageurl'] = url.text;
            
            print(data);
            events_collection.insert(data);
            print("Event added");
        

    print(city + " added");

print("all completed");




#request = http.request('GET', "http://api.eventful.com/rest/events/rss?app_key=zrKgdfNwMBtMJ2nV&date=today");
#print("Request complete");


#root = tree.getroot();

#print(root.tag);





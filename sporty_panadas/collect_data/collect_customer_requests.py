import os
import sys
from time import sleep

import openai
from dotenv import load_dotenv
import random

load_dotenv("tokens.env")

openai.api_key = os.getenv("OPENAI_API_KEY")

examples = [
    "Our project is focused on designing an online bookstore where users can browse and purchase books, and also leave reviews. The system should also enable administrative staff to manage the inventory, track orders, and respond to user inquiries.",
    "We're working on a fitness app that allows users to track their workouts, set fitness goals, connect with personal trainers, and share their progress on social media. Additionally, we need a backend system for administrators to manage users, trainers, and premium content.",
    "Create an online food delivery service where customers can browse restaurants, place orders, and track delivery, while restaurant owners and drivers need functionality for managing orders and deliveries.",
    "A virtual classroom system, where teachers can schedule classes, share materials, and evaluate student work, while students can attend classes, submit assignments, and communicate with peers and teachers.",
    "Please craft a telemedicine platform, where patients can book appointments, consult with healthcare professionals over video, and receive prescriptions, while doctors manage their schedules, patient records, and consultations.",
    "Build an event management platform, where event planners can create and manage events, ticketing and registration, while attendees can search for events, register, and receive notifications.",
    "The aim is to design an e-commerce website where users can browse and purchase electronic products, leave reviews, and track orders, with a backend for administrators to manage inventory, handle refunds, and respond to customer queries.",
    "We are designing a hotel booking system that allows users to search for hotels, book rooms, and provide feedback, while hotel administrators manage room availability, rates, and customer inquiries.",
    "Our project is a movie streaming platform where viewers can watch movies, provide ratings, and leave reviews, while administrators manage the movie library, subscriptions, and user management.",
    "Set up a real estate platform, where users can search and view property listings, request viewings, and communicate with agents, while agents can upload listings, manage clients, and track commissions.",
    "We're creating a banking app that allows customers to check balances, transfer money, and pay bills, while bank staff can manage accounts, loans, and customer inquiries.",
    "Our mission involves a project management tool, where project team members can create tasks, set deadlines, and share updates, while project managers can track progress, allocate resources, and manage teams.",
    "Create an online learning platform where students can enroll in courses, watch video lessons, and take quizzes, while teachers create and manage courses, grade assignments, and interact with students.",
    "Wa are building a sports team management app, where team members can track game schedules, view statistics, and communicate with each other, while coaches manage rosters, strategies, and player development.",
    "Designing a charity fundraising platform, where users can discover causes, make donations, and track the impact of their contributions, while charity organizers can manage fundraising campaigns and donor relationships.",
    "Please craft a social media app where users can create profiles, post updates, and connect with friends, and where administrators can monitor content, manage user reports, and handle advertising.",
    "A car rental platform where customers can browse cars, book rentals, and leave reviews, while rental staff manage vehicle availability, pricing, and customer inquiries.",
    "CREATE a pet care service platform, where pet owners can book services like dog walking or vet appointments, and where service providers manage their schedules, prices, and customer interactions.",
    "Design an online auction system where users can bid on items, track auctions, and manage their bids, while administrators oversee auctions, manage inventory, and handle disputes.",
    "Create a weather forecasting app where users can check local and global weather, get alerts, and customize their viewing preferences, while meteorologists manage data input, forecast models, and user inquiries.",
    "We're creating a travel booking website, where users can search for flights, hotels, and holiday packages, and book their trips, while travel agents manage bookings, pricing, and customer inquiries.",
    "We're developing a job recruitment platform where job seekers can create profiles, apply for jobs, and track applications, while employers post job openings, screen applicants, and manage recruitment processes.",
    "Our project involves a digital library where users can search for books, borrow digital copies, and leave reviews, while librarians manage book catalogues, user accounts, and late returns.",
    "We're designing a music streaming app where users can listen to songs, create playlists, and follow artists, while music labels manage their artists, albums, and royalty payments.",
    "We're focused on a ride-hailing app, where users can request rides, track their driver, and rate their experience, while drivers can accept ride requests, navigate routes, and manage earnings.",
    "Our project involves a gaming platform, where gamers can play games, interact with other players, and purchase in-game items, while game developers manage game releases, user behavior, and community engagement.",
    "We're building an online grocery store where customers can browse items, place orders, and schedule deliveries, while store managers manage inventory, pricing, and delivery schedules.",
    "We're creating a fitness challenge app, where users can join challenges, track their progress, and earn rewards, while fitness coaches create challenges, track user progress, and manage rewards.",
    "Our project involves a dating app where users can create profiles, browse potential matches, and communicate with other users, while administrators manage user safety, dispute resolution, and advertising.",
    "We're working on a cloud storage service where users can upload, share, and manage files, while system administrators monitor storage usage, manage server health, and handle security.",
    "Our project is a photo sharing app where users can post photos, follow others, and like or comment on posts, while administrators manage content moderation, user reports, and advertising.",
    "We're creating a digital wallet app, where users can make transactions, store cards, and track expenses, while administrators manage security, dispute resolution, and partnerships with businesses."
]

system = "You are a client in a company requesting an UML model for one of your projects.\n" \
         "Describe shortly in one or two sentences in everyday language what your own project should include, such that a non UML expert could understand this.\n\n" \
         "One Example for another project could look like this:\n"
if __name__ == '__main__':
    count = 0
    for i in range(10000):
        try:
            print("###")
            example = random.choice(examples)

            completion = openai.ChatCompletion.create(
                model="gpt-3.5-turbo",
                messages=[
                    {"role": "system", "content": f"{system}{example}"},
                ],
                max_tokens=256,
                temperature=1.3,
                top_p=1,
                frequency_penalty=0,
                presence_penalty=0.3,
            )
            answer = completion.choices[0].message["content"]
            print(answer, flush=True)
            count = 0
        except Exception as e:
            count += 1
            print(f"ERROR {count}", file=sys.stderr)
            print(e, file=sys.stderr)
            sleep(10)
